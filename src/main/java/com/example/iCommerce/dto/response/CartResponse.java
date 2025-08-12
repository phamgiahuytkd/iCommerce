package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    String id;
    String product_variant_id;
    String product_id;
    String name;
    List<ProductVariantAttributeValueResponse> attribute_values;
    Long price;
    String image;
    Long quantity;
    Long stock;
    String brand_id;
    Integer percent;
    GiftResponse gift;


}
