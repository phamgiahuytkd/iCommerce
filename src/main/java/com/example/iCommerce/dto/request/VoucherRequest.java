package com.example.iCommerce.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherRequest {
    String code;
    String description;
    String voucher_type;
    Integer percent;
    Long max_amount;
    Long min_order_amount;
    LocalDateTime start_day;
    LocalDateTime end_day;
    Integer usage_limit;
    Integer used_count;
}
