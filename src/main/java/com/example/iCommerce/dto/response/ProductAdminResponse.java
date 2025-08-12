package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductAdminResponse {
    String id;
    String name;
    String brand;
    String category;
    String image;
    LocalDateTime create_day;
    String description;
    String instruction;
    String ingredient;
    Double star;
    Long sold;
    Long comment;
    Long view;

}
