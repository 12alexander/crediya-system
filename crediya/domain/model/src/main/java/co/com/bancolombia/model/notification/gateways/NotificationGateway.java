package co.com.bancolombia.model.notification.gateways;

import co.com.bancolombia.model.orders.Orders;
import reactor.core.publisher.Mono;

public interface NotificationGateway {
    
    Mono<Void> notifyOrderDecision(Orders order);
}