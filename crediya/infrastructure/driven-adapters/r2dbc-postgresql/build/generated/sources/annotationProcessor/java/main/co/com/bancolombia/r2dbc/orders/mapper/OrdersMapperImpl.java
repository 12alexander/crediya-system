package co.com.bancolombia.r2dbc.orders.mapper;

import co.com.bancolombia.model.orders.Orders;
import co.com.bancolombia.r2dbc.orders.data.OrdersData;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-10T02:21:44-0500",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 17.0.16 (Ubuntu)"
)
@Component
public class OrdersMapperImpl implements OrdersMapper {

    @Override
    public OrdersData toData(Orders orders) {
        if ( orders == null ) {
            return null;
        }

        OrdersData.OrdersDataBuilder ordersData = OrdersData.builder();

        ordersData.id( orders.getId() );
        ordersData.amount( orders.getAmount() );
        ordersData.deadline( orders.getDeadline() );
        ordersData.emailAddress( orders.getEmailAddress() );
        ordersData.creationDate( orders.getCreationDate() );
        ordersData.updateDate( orders.getUpdateDate() );
        ordersData.idStatus( orders.getIdStatus() );
        ordersData.idLoanType( orders.getIdLoanType() );

        return ordersData.build();
    }

    @Override
    public Orders toDomain(OrdersData ordersData) {
        if ( ordersData == null ) {
            return null;
        }

        Orders.OrdersBuilder orders = Orders.builder();

        orders.id( ordersData.getId() );
        orders.amount( ordersData.getAmount() );
        orders.deadline( ordersData.getDeadline() );
        orders.emailAddress( ordersData.getEmailAddress() );
        orders.idStatus( ordersData.getIdStatus() );
        orders.idLoanType( ordersData.getIdLoanType() );
        orders.creationDate( ordersData.getCreationDate() );
        orders.updateDate( ordersData.getUpdateDate() );

        return orders.build();
    }
}
