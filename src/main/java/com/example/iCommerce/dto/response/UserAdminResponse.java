package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAdminResponse {
    String id;
    String email;
    String name;
    String phone;
    Integer reputation;
    Long order_placed;
    Long expend;
    String status;
    LocalDateTime latest_order_date;

}
