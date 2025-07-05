package com.example.iCommerce.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderNowRequest {
    String name;
    Long amount;
    String address;
    String fulladdress;
    String note;
    String phone;
    String payment;
    String status;
    String device;
    List<CartRequest> carts;
}
