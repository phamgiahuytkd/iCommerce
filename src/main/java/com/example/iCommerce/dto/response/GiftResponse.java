package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiftResponse {
    String id;
    String product_variant_id;
    String name;
    String image;
    List<AttributeValueResponse> attribute_values;
}
