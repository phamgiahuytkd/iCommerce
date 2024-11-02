package com.example.iCommerce.dto.response;

import com.example.iCommerce.entity.Products;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrdersResponse {
    String id;
    Map<String, OrderDetailResponse> productsList;
    Long amount;
    String shipping_address;
    String order_phone;
    String order_date;
    String order_status;
}
