package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.OrderStatusRequest;
import com.example.iCommerce.dto.response.OrderStatusResponse;
import com.example.iCommerce.entity.OrderStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderStatusMapper {
    OrderStatus toOrderStatus(OrderStatusRequest request);
    OrderStatusResponse toOderStatusResponse(OrderStatus orderStatus);
}
