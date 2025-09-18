package co.com.bancolombia.api;

import co.com.bancolombia.api.handler.OrderHandler;
import co.com.bancolombia.api.handler.ReportHandler;
import co.com.bancolombia.api.handler.DebtCapacityHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Simple test to verify router configuration loads correctly.
 */
class SimpleRouterTest {

    @Test
    void routerRestBasicTest() {
        OrderHandler orderHandler = mock(OrderHandler.class);
        ReportHandler reportHandler = mock(ReportHandler.class);
        DebtCapacityHandler debtCapacityHandler = mock(DebtCapacityHandler.class);
        RouterRest routerRest = new RouterRest(orderHandler, reportHandler, debtCapacityHandler);
        assertNotNull(routerRest);
    }
}