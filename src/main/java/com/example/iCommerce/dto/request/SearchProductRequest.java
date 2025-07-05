package com.example.iCommerce.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchProductRequest {
    String name;
    Long min_price;
    Long max_price;
    String brand_id;
    String category_id;
}
