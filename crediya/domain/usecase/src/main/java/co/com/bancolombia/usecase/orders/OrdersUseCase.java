package co.com.bancolombia.usecase.orders;

import co.com.bancolombia.model.constants.ValidationMessages;
import co.com.bancolombia.model.enums.StatusEnum;
import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.notification.gateways.NotificationGateway;
import co.com.bancolombia.model.orders.Orders;
import co.com.bancolombia.model.orders.PendingRequest;
import co.com.bancolombia.model.orders.exceptions.InvalidLoanAmountException;
import co.com.bancolombia.model.orders.exceptions.LoanTypeNotFoundException;
import co.com.bancolombia.model.orders.exceptions.OrdersBusinessException;
import co.com.bancolombia.model.orders.gateways.OrdersRepository;
import co.com.bancolombia.usecase.orders.interfaces.IOrdersUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class OrdersUseCase implements IOrdersUseCase {
    
    private final OrdersRepository ordersRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final NotificationGateway notificationGateway;

        public Mono<Orders> createLoanRequest(String idUser, BigDecimal amount, Integer deadline,
                                        String emailAddress, String loanTypeId) {
        
        return validateLoanType(loanTypeId)
                .flatMap(loanType -> {
                    validateLoanAmountSync(amount, loanType);
                    return getPendingStatusId()
                            .flatMap(pendingStatusId -> createAndValidateOrder(
                                    idUser, amount, deadline, emailAddress, loanTypeId, pendingStatusId))
                            .flatMap(this::saveOrder);
                });
    }

    private Mono<LoanType> validateLoanType(String loanTypeId) {
        return loanTypeRepository.findById(loanTypeId)
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(loanTypeId)));
    }

    private Mono<Void> validateLoanAmount(BigDecimal amount, LoanType loanType) {
        return Mono.fromRunnable(() -> {
            validateLoanAmountSync(amount, loanType);
        });
    }

    private void validateLoanAmountSync(BigDecimal amount, LoanType loanType) {
        if (!loanType.isAmountValid(amount)) {
            throw new InvalidLoanAmountException(amount, loanType.getMinimumAmount(), loanType.getMaximumAmount());
        }
    }

    private Mono<String> getPendingStatusId() {
        return ordersRepository.findPendingStatusId()
                .switchIfEmpty(Mono.error(new OrdersBusinessException("PENDING_STATUS_NOT_FOUND",
                                                                     ValidationMessages.PENDING_STATUS_NOT_FOUND)));
    }

    private Mono<Orders> createAndValidateOrder(String idUser, BigDecimal amount, Integer deadline,
                                              String emailAddress, String loanTypeId, String pendingStatusId) {
        return Mono.fromCallable(() -> {
            Orders order = Orders.createNew(amount, deadline, emailAddress, loanTypeId, pendingStatusId);
            order.validateForCreation();
            return order;
        });
    }

    private Mono<Orders> saveOrder(Orders order) {
        return ordersRepository.save(order);
    }

    private Mono<Orders> sendDecisionNotification(Orders order) {
        return notificationGateway.notifyOrderDecision(order)
                .then(Mono.just(order))
                .onErrorReturn(order);
    }

    public Mono<Orders> findById(String orderId) {
        return ordersRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new OrdersBusinessException("ORDER_NOT_FOUND",
                                                                     ValidationMessages.ORDER_NOT_FOUND + orderId)));
    }

    @Override
    public Flux<Orders> findByEmailAddress(String emailAddress) {
        return ordersRepository.findByEmailAddress(emailAddress);
    }

    @Override
    public Flux<PendingRequest> findPendingRequests(UUID statusId, String email, int page, int size) {
        return ordersRepository.findPendingRequests(statusId, email, page, size);
    }

    @Override
    public Mono<Orders> updateOrderDecision(String orderId, String decision) {
        return findById(orderId)
                .flatMap(order -> validateOrderCanBeUpdated(order))
                .flatMap(order -> getNewStatusId(decision)
                        .flatMap(newStatusId -> updateOrderWithNewStatus(order, newStatusId)))
                .flatMap(this::saveOrder)
                .flatMap(this::sendDecisionNotification);
    }

    private Mono<Orders> validateOrderCanBeUpdated(Orders order) {
        String pendingStatusId = StatusEnum.PENDING.getId();
        if (!pendingStatusId.equals(order.getIdStatus())) {
            return Mono.error(new OrdersBusinessException("ORDER_ALREADY_PROCESSED",
                    ValidationMessages.ORDER_ALREADY_PROCESSED));
        }
        return Mono.just(order);
    }

    private Mono<String> getNewStatusId(String decision) {
        return Mono.fromCallable(() -> {
            return switch (decision) {
                case "APPROVED" -> StatusEnum.APPROVED.getId();
                case "REJECTED" -> StatusEnum.REJECTED.getId();
                default -> throw new IllegalArgumentException(ValidationMessages.INVALID_DECISION + decision);
            };
        });
    }

    private Mono<Orders> updateOrderWithNewStatus(Orders order, String newStatusId) {
        return Mono.fromCallable(() -> order.toBuilder()
                .idStatus(newStatusId)
                .updateDate(LocalDateTime.now())
                .build());
    }
}