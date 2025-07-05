package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;
    String name;
    Long amount;
    String address;  
    String fulladdress;
    String note;
    String phone;
    LocalDateTime date;
    String status;
    String payment;
    String device;
    String user_id;
}
