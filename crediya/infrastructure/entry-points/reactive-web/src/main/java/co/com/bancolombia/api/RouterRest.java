package co.com.bancolombia.api;

import co.com.bancolombia.api.handler.OrderHandler;
import co.com.bancolombia.api.handler.ReportHandler;
import co.com.bancolombia.api.handler.DebtCapacityHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    
    private final OrderHandler orderHandler;
    private final ReportHandler reportHandler;
    private final DebtCapacityHandler debtCapacityHandler;

    public RouterRest(OrderHandler orderHandler, ReportHandler reportHandler, DebtCapacityHandler debtCapacityHandler) {
        this.orderHandler = orderHandler;
        this.reportHandler = reportHandler;
        this.debtCapacityHandler = debtCapacityHandler;
    }
    
    private static final String API_V1 = "/api/v1";
    private static final String SOLICITUD_PATH = API_V1 + "/solicitud";
    private static final String CALCULAR_CAPACIDAD_PATH = API_V1 + "/calcular-capacidad";


    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return route(POST(SOLICITUD_PATH)
                        .and(accept(MediaType.APPLICATION_JSON)), 
                orderHandler::createLoanRequest)
                .andRoute(GET(SOLICITUD_PATH + "/{id}"), 
                        orderHandler::getLoanRequest)
                .andRoute(PUT(SOLICITUD_PATH + "/{id}/decision")
                        .and(accept(MediaType.APPLICATION_JSON)),
                        orderHandler::updateOrderDecision)
                .andRoute(GET(API_V1 + "/solicitudes-pendientes"),
                        reportHandler::getPendingRequests)
                .andRoute(POST(CALCULAR_CAPACIDAD_PATH)
                        .and(accept(MediaType.APPLICATION_JSON)),
                        debtCapacityHandler::calculateDebtCapacity);
    }
}