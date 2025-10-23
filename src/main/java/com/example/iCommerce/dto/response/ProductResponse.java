package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String id;
    String name;
    Long price;
    String brand;
    String category;
    String image;
    Long stock;
    Long view;
    LocalDateTime create_day;
    Integer percent;
    String gift_id;
    String gift_name;
    String gift_image;
    Long gift_stock;
    LocalDateTime gift_start_day;
    LocalDateTime gift_end_day;
    Double star;

    // ➕ Thêm mới
    String description;
    String instruction;
    String ingredient;
    Boolean stop;

}
