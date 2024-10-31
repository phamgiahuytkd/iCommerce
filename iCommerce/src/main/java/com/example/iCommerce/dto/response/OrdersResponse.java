package com.example.iCommerce.dto.response;

import com.example.iCommerce.entity.Products;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrdersResponse {
    String id;
    List<Products> products;
    Long amount;
    String shipping_address;
    String order_phone;
    LocalDateTime order_date;
    String order_status;
}
