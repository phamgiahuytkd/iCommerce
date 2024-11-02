package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.OrdersCreationRequest;
import com.example.iCommerce.dto.request.ProductsCreationRequest;
import com.example.iCommerce.dto.request.ProductsUpdateRequest;
import com.example.iCommerce.dto.response.OrderDetailResponse;
import com.example.iCommerce.dto.response.OrdersResponse;
import com.example.iCommerce.dto.response.ProductsResponse;
import com.example.iCommerce.dto.response.SummaryOrdersResponse;
import com.example.iCommerce.entity.Orders;
import com.example.iCommerce.entity.Products;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
    public interface OrdersMapper {

    @Mapping(target = "products", expression = "java(mapProducts(request.getProducts()))")
    Orders toOrders(OrdersCreationRequest request);
    OrdersResponse toOrdersResponse(Orders orders);
    SummaryOrdersResponse toSummaryOrdersResponse(Orders orders);


    default String mapProducts(List<String> products) {
        return products != null ? String.join(",", products) : null;
    }



    default String localDateTimeToString(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }

//        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//        void updateProducts(@MappingTarget Products products, ProductsUpdateRequest request);
    }
