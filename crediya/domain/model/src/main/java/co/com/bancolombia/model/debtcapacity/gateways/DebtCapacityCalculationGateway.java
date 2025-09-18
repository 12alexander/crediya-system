package co.com.bancolombia.model.debtcapacity.gateways;

import co.com.bancolombia.model.debtcapacity.DebtCapacity;
import reactor.core.publisher.Mono;

public interface DebtCapacityCalculationGateway {

    Mono<Void> sendCalculationRequest(DebtCapacity debtCapacity);
}