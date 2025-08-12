package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
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
    List<ProductVariantAttributeValueResponse> attribute_values;
    Long stock;
    LocalDateTime start_day;
    LocalDateTime end_day;
}
