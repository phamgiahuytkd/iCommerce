package com.example.iCommerce.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MoMoMethodRequest {
    String orderId;
    Long amount;
    String orderInfo;
    String requestId;
}
