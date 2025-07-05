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
public class ProductVariantResponse {
    String id;
    String name;
    List<AttributeValueResponse> attribute_values;
    Long price;
    String description;
    String instruction;
    String ingredient;
    String image;
    String images;
    Long stock;
    LocalDateTime create_day;
    LocalDateTime stop_day;
    Integer percent;
    LocalDateTime start_day;
    LocalDateTime end_day;
    Double star;
    Long rating_quantity;
}
