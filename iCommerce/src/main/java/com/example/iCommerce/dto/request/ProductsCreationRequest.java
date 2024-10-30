package com.example.iCommerce.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductsCreationRequest {
    String name;
    long price;
    String brand;
    String colour;
    String image;
    String created_by;
    LocalDateTime created_date;
    int stock;
}
