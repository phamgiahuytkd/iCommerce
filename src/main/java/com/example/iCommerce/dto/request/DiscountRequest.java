package com.example.iCommerce.dto.request;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountRequest {
    Integer percent;
    LocalDateTime start_day;
    LocalDateTime end_day;
}
