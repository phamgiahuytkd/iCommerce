package com.example.iCommerce.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiftRequest {
    String product_variant_ids;
    String product_variant_id;
    Long stock;
    LocalDateTime start_day;
    LocalDateTime end_day;
}
