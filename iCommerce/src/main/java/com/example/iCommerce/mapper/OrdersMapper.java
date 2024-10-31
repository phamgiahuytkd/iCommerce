package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.OrdersCreationRequest;
import com.example.iCommerce.dto.request.ProductsCreationRequest;
import com.example.iCommerce.dto.request.ProductsUpdateRequest;
import com.example.iCommerce.dto.response.OrdersResponse;
import com.example.iCommerce.dto.response.ProductsResponse;
import com.example.iCommerce.entity.Orders;
import com.example.iCommerce.entity.Products;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
    public interface OrdersMapper {
    Orders toOrders(OrdersCreationRequest request);
    OrdersResponse toOrdersResponse(Orders orders);

        default String localDateTimeToString(LocalDateTime date) {
            if (date == null) {
                return null;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return date.format(formatter);
        }

        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        void updateProducts(@MappingTarget Products products, ProductsUpdateRequest request);
    }
