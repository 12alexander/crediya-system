package co.com.bancolombia.usecase.debtcapacity.interfaces;

import co.com.bancolombia.model.debtcapacity.DebtCapacity;
import reactor.core.publisher.Mono;

public interface IDebtCapacityUseCase {

    Mono<DebtCapacity> processDebtCapacityRequest(DebtCapacity debtCapacity);
}