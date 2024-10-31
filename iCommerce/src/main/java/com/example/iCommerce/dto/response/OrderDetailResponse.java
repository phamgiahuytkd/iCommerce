package com.example.iCommerce.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailResponse {
    String product_id;
    String name;
    Integer quantity;
    Long price;
    Long amount;

}
