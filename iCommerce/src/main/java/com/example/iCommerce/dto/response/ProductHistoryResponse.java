package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductHistoryResponse {
    String product_id;
    Long price;
    String created_by;
    LocalDateTime created_date;
}
