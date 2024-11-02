package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SummaryOrdersResponse {
    String id;
    Long amount;
    String shipping_address;
    String order_phone;
    String order_date;
    String order_status;
}
