package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductToRateResponse {
    String id;
    String name;
    String image;
    String order_id;
    List<AttributeValueResponse> attribute_values;
}
