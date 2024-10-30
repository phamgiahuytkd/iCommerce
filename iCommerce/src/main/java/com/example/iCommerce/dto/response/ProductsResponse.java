package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductsResponse {
    String id;
    String name;
    long price;
    String brand;
    String colour;
    String image;
    String created_by;
    String created_date;
    int stock;
}
