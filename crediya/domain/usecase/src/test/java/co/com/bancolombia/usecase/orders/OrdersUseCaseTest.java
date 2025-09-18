package co.com.bancolombia.usecase.orders;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for OrdersUseCase.
 * Tests business logic and error handling scenarios.
 */
@ExtendWith(MockitoExtension.class)
class OrdersUseCaseTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private NotificationGateway notificationGateway;

    private OrdersUseCase ordersUseCase;

    @BeforeEach
    void setUp() {
        ordersUseCase = new OrdersUseCase(ordersRepository, loanTypeRepository, notificationGateway);
    }

    private LoanType buildValidLoanType() {
        return LoanType.builder()
                .id("550e8400-e29b-41d4-a716-446655441003")
                .name("MICROCREDITO")
                .minimumAmount(new BigDecimal("10000"))
                .maximumAmount(new BigDecimal("500000"))
                .interestRate(new BigDecimal("12.5"))
                .automaticValidation(true)
                .build();
    }

    private Orders buildValidOrder() {
        return Orders.builder()
                .id("order-123")
                .amount(new BigDecimal("50000"))
                .deadline(24)
                .emailAddress("test@example.com")
                .idLoanType("550e8400-e29b-41d4-a716-446655441003")
                .idStatus("pending-status-id")
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Create loan request - success")
    void createLoanRequestSuccess() {
        // Arrange
        String documentId = "12345678";
        BigDecimal amount = new BigDecimal("50000");
        Integer deadline = 24;
        String emailAddress = "test@example.com";
        String loanTypeId = "550e8400-e29b-41d4-a716-446655441003";
        String pendingStatusId = "pending-status-id";

        LoanType loanType = buildValidLoanType();
        Orders expectedOrder = buildValidOrder();

        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(ordersRepository.findPendingStatusId()).thenReturn(Mono.just(pendingStatusId));
        when(ordersRepository.save(any(Orders.class))).thenReturn(Mono.just(expectedOrder));

        StepVerifier.create(ordersUseCase.createLoanRequest(documentId, amount, deadline, emailAddress, loanTypeId))
                .expectNext(expectedOrder)
                .verifyComplete();
    }

    @Test
    @DisplayName("Create loan request - loan type not found")
    void createLoanRequestLoanTypeNotFound() {
        // Arrange
        String documentId = "12345678";
        BigDecimal amount = new BigDecimal("50000");
        Integer deadline = 24;
        String emailAddress = "test@example.com";
        String loanTypeId = "invalid-loan-type-id";

        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.empty());


        StepVerifier.create(ordersUseCase.createLoanRequest(documentId, amount, deadline, emailAddress, loanTypeId))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Create loan request - invalid amount (too low)")
    void createLoanRequestInvalidAmountTooLow() {
        String documentId = "12345678";
        BigDecimal amount = new BigDecimal("5000"); // Below minimum
        Integer deadline = 24;
        String emailAddress = "test@example.com";
        String loanTypeId = "550e8400-e29b-41d4-a716-446655441003";

        LoanType loanType = buildValidLoanType();

        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));

        StepVerifier.create(ordersUseCase.createLoanRequest(documentId, amount, deadline, emailAddress, loanTypeId))
                .expectError(InvalidLoanAmountException.class)
                .verify();
    }

    @Test
    @DisplayName("Create loan request - invalid amount (too high)")
    void createLoanRequestInvalidAmountTooHigh() {
        String documentId = "12345678";
        BigDecimal amount = new BigDecimal("600000"); // Above maximum
        Integer deadline = 24;
        String emailAddress = "test@example.com";
        String loanTypeId = "550e8400-e29b-41d4-a716-446655441003";

        LoanType loanType = buildValidLoanType();

        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));

        StepVerifier.create(ordersUseCase.createLoanRequest(documentId, amount, deadline, emailAddress, loanTypeId))
                .expectError(InvalidLoanAmountException.class)
                .verify();
    }

    @Test
    @DisplayName("Create loan request - pending status not found")
    void createLoanRequestPendingStatusNotFound() {
        // Arrange
        String documentId = "12345678";
        BigDecimal amount = new BigDecimal("50000");
        Integer deadline = 24;
        String emailAddress = "test@example.com";
        String loanTypeId = "550e8400-e29b-41d4-a716-446655441003";

        LoanType loanType = buildValidLoanType();

        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));
        when(ordersRepository.findPendingStatusId()).thenReturn(Mono.empty());

        StepVerifier.create(ordersUseCase.createLoanRequest(documentId, amount, deadline, emailAddress, loanTypeId))
                .expectError(OrdersBusinessException.class)
                .verify();
    }

    @Test
    @DisplayName("Find by ID - success")
    void findByIdSuccess() {
        String orderId = "order-123";
        Orders expectedOrder = buildValidOrder();

        when(ordersRepository.findById(orderId)).thenReturn(Mono.just(expectedOrder));

        StepVerifier.create(ordersUseCase.findById(orderId))
                .expectNext(expectedOrder)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find by ID - not found")
    void findByIdNotFound() {
        // Arrange
        String orderId = "non-existing-order";

        when(ordersRepository.findById(orderId)).thenReturn(Mono.empty());


        StepVerifier.create(ordersUseCase.findById(orderId))
                .expectError(OrdersBusinessException.class)
                .verify();
    }


    @Test
    @DisplayName("Find by email address - success")
    void findByEmailAddressSuccess() {

        String emailAddress = "test@example.com";
        Orders order1 = buildValidOrder();
        Orders order2 = order1.toBuilder().id("order-456").build();

        when(ordersRepository.findByEmailAddress(emailAddress)).thenReturn(Flux.just(order1, order2));


        StepVerifier.create(ordersUseCase.findByEmailAddress(emailAddress))
                .expectNext(order1)
                .expectNext(order2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find by email address - empty result")
    void findByEmailAddressEmpty() {

        String emailAddress = "nonexistent@example.com";

        when(ordersRepository.findByEmailAddress(emailAddress)).thenReturn(Flux.empty());


        StepVerifier.create(ordersUseCase.findByEmailAddress(emailAddress))
                .verifyComplete();
    }

    @Test
    @DisplayName("Find pending requests - success")
    void findPendingRequestsSuccess() {
        UUID statusId = UUID.randomUUID();
        String email = "test@example.com";
        int page = 0;
        int size = 10;

        PendingRequest pendingRequest = PendingRequest.builder()
                .amount(new BigDecimal("50000"))
                .deadline(24)
                .emailAddress(email)
                .name("Test User")
                .loanType("MICROCREDITO")
                .build();

        when(ordersRepository.findPendingRequests(statusId, email, page, size))
                .thenReturn(Flux.just(pendingRequest));

        StepVerifier.create(ordersUseCase.findPendingRequests(statusId, email, page, size))
                .expectNext(pendingRequest)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find pending requests - empty result")
    void findPendingRequestsEmpty() {
        UUID statusId = UUID.randomUUID();
        String email = "test@example.com";
        int page = 0;
        int size = 10;

        when(ordersRepository.findPendingRequests(statusId, email, page, size))
                .thenReturn(Flux.empty());

        StepVerifier.create(ordersUseCase.findPendingRequests(statusId, email, page, size))
                .verifyComplete();
    }

    @Test
    @DisplayName("Update order decision - approve success")
    void updateOrderDecisionApproveSuccess() {
        String orderId = "order-123";
        String decision = "APPROVED";

        Orders pendingOrder = buildValidOrder().toBuilder()
                .idStatus(StatusEnum.PENDING.getId())
                .build();

        Orders approvedOrder = pendingOrder.toBuilder()
                .idStatus(StatusEnum.APPROVED.getId())
                .build();

        when(ordersRepository.findById(orderId)).thenReturn(Mono.just(pendingOrder));
        when(ordersRepository.save(any(Orders.class))).thenReturn(Mono.just(approvedOrder));
        when(notificationGateway.notifyOrderDecision(any(Orders.class))).thenReturn(Mono.empty());

        StepVerifier.create(ordersUseCase.updateOrderDecision(orderId, decision))
                .expectNext(approvedOrder)
                .verifyComplete();
    }

    @Test
    @DisplayName("Update order decision - reject success")
    void updateOrderDecisionRejectSuccess() {
        String orderId = "order-123";
        String decision = "REJECTED";

        Orders pendingOrder = buildValidOrder().toBuilder()
                .idStatus(StatusEnum.PENDING.getId())
                .build();

        Orders rejectedOrder = pendingOrder.toBuilder()
                .idStatus(StatusEnum.REJECTED.getId())
                .build();

        when(ordersRepository.findById(orderId)).thenReturn(Mono.just(pendingOrder));
        when(ordersRepository.save(any(Orders.class))).thenReturn(Mono.just(rejectedOrder));
        when(notificationGateway.notifyOrderDecision(any(Orders.class))).thenReturn(Mono.empty());

        StepVerifier.create(ordersUseCase.updateOrderDecision(orderId, decision))
                .expectNext(rejectedOrder)
                .verifyComplete();
    }

    @Test
    @DisplayName("Update order decision - order not found")
    void updateOrderDecisionOrderNotFound() {
        String orderId = "non-existing-order";
        String decision = "APPROVED";

        when(ordersRepository.findById(orderId)).thenReturn(Mono.empty());

        StepVerifier.create(ordersUseCase.updateOrderDecision(orderId, decision))
                .expectError(OrdersBusinessException.class)
                .verify();
    }

    @Test
    @DisplayName("Update order decision - order already processed")
    void updateOrderDecisionOrderAlreadyProcessed() {
        String orderId = "order-123";
        String decision = "APPROVED";

        Orders processedOrder = buildValidOrder().toBuilder()
                .idStatus(StatusEnum.APPROVED.getId())
                .build();

        when(ordersRepository.findById(orderId)).thenReturn(Mono.just(processedOrder));

        StepVerifier.create(ordersUseCase.updateOrderDecision(orderId, decision))
                .expectError(OrdersBusinessException.class)
                .verify();
    }

    @Test
    @DisplayName("Update order decision - invalid decision")
    void updateOrderDecisionInvalidDecision() {
        String orderId = "order-123";
        String decision = "INVALID_DECISION";

        Orders pendingOrder = buildValidOrder().toBuilder()
                .idStatus(StatusEnum.PENDING.getId())
                .build();

        when(ordersRepository.findById(orderId)).thenReturn(Mono.just(pendingOrder));

        StepVerifier.create(ordersUseCase.updateOrderDecision(orderId, decision))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

}