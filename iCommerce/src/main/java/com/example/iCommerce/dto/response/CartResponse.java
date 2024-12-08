package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    String id;
    String product_id;
    String name;
    Long price;
    String image;
    String colour;
}
