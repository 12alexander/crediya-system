package co.com.bancolombia.config;

import co.com.bancolombia.model.debtcapacity.gateways.DebtCapacityCalculationGateway;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.model.notification.gateways.NotificationGateway;
import co.com.bancolombia.model.orders.gateways.OrdersRepository;
import co.com.bancolombia.usecase.debtcapacity.DebtCapacityUseCase;
import co.com.bancolombia.usecase.debtcapacity.interfaces.IDebtCapacityUseCase;
import co.com.bancolombia.usecase.orders.OrdersUseCase;
import co.com.bancolombia.usecase.orders.interfaces.IOrdersUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    /**
     * Creates the Orders use case bean.
     * 
     * @param ordersRepository repository for order operations
     * @param loanTypeRepository repository for loan type operations
     * @return IOrdersUseCase implementation
     */
    @Bean
    public IOrdersUseCase ordersUseCase(OrdersRepository ordersRepository,
                                       LoanTypeRepository loanTypeRepository,
                                       @Autowired(required = false) NotificationGateway notificationGateway,
                                       @Autowired(required = false) DebtCapacityCalculationGateway debtCapacityCalculationGateway) {
        return new OrdersUseCase(ordersRepository, loanTypeRepository, notificationGateway, debtCapacityCalculationGateway);
    }

    @Bean
    public IDebtCapacityUseCase debtCapacityUseCase(@Autowired(required = false) DebtCapacityCalculationGateway calculationGateway) {
        return new DebtCapacityUseCase(calculationGateway);
    }
}
