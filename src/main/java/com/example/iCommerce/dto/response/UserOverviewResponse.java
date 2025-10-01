package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserOverviewResponse {
    Long total_accumulated_money, total_orders, processing_orders, success_orders, failed_orders, fraud_orders;
}
