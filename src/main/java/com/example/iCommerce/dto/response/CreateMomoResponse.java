package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMomoResponse {
    String partnerCode;
    String orderId;
    String requestId;
    Long amount;
    Long responseTime;
    String message;
    Integer resultCode;
    String payUrl;
    String deeplink;
    String qrCodeUrl;

}
