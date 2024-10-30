package com.example.iCommerce.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductsUpdateRequest {
    String name;
    Long price;
    String brand;
    String colour;
    String image;
    LocalDateTime created_date;
    int stock;
}
