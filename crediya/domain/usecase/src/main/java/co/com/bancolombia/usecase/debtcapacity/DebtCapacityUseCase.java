package co.com.bancolombia.usecase.debtcapacity;

import co.com.bancolombia.model.constants.ValidationMessages;
import co.com.bancolombia.model.debtcapacity.DebtCapacity;
import co.com.bancolombia.model.debtcapacity.gateways.DebtCapacityCalculationGateway;
import co.com.bancolombia.usecase.debtcapacity.interfaces.IDebtCapacityUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class DebtCapacityUseCase implements IDebtCapacityUseCase {

    private final DebtCapacityCalculationGateway calculationGateway;

    @Override
    public Mono<DebtCapacity> processDebtCapacityRequest(DebtCapacity debtCapacity) {
        return Mono.just(debtCapacity)
                .map(this::assignId)
                .flatMap(this::sendToCalculation)
                .doOnNext(processed -> System.out.println(ValidationMessages.DEBT_CAPACITY_PROCESSED + " ID: " + processed.getId()))
                .doOnError(error -> System.err.println(ValidationMessages.CAPACITY_CALCULATION_FAILED + " OrderID: " + debtCapacity.getOrderId()));
    }

    private DebtCapacity assignId(DebtCapacity debtCapacity) {
        return debtCapacity.toBuilder()
                .id(UUID.randomUUID().toString())
                .build();
    }

    private Mono<DebtCapacity> sendToCalculation(DebtCapacity debtCapacity) {
        return calculationGateway.sendCalculationRequest(debtCapacity)
                .then(Mono.just(debtCapacity))
                .onErrorResume(error -> {
                    System.err.println(ValidationMessages.CAPACITY_CALCULATION_FAILED + ": " + error.getMessage());
                    return Mono.just(debtCapacity);
                });
    }
}