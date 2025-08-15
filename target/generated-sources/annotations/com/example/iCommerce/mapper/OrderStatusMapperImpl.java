package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.OrderStatusRequest;
import com.example.iCommerce.dto.response.OrderStatusResponse;
import com.example.iCommerce.entity.OrderStatus;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class OrderStatusMapperImpl implements OrderStatusMapper {

    @Override
    public OrderStatus toOrderStatus(OrderStatusRequest request) {
        if ( request == null ) {
            return null;
        }

        OrderStatus.OrderStatusBuilder orderStatus = OrderStatus.builder();

        orderStatus.status( request.getStatus() );
        orderStatus.description( request.getDescription() );

        return orderStatus.build();
    }

    @Override
    public OrderStatusResponse toOderStatusResponse(OrderStatus orderStatus) {
        if ( orderStatus == null ) {
            return null;
        }

        OrderStatusResponse.OrderStatusResponseBuilder orderStatusResponse = OrderStatusResponse.builder();

        orderStatusResponse.status( orderStatus.getStatus() );
        orderStatusResponse.description( orderStatus.getDescription() );
        orderStatusResponse.update_day( orderStatus.getUpdate_day() );

        return orderStatusResponse.build();
    }
}
