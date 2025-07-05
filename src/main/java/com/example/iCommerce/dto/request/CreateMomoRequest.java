package com.example.iCommerce.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMomoRequest {
    String partnerCode;
    String requestType;
    String ipnUrl;
    String orderId;
    Long amount;
    String orderInfo;
    String requestId;
    String redirectUrl;
    String lang;
    String extraData;
    String signature;

}
