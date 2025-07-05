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
public class UserResponse {
    String id;
    String email;
    String full_name;
    String phone;
    AddressResponse default_shipping_address;
    LocalDate date_of_birth;
    String avatar;
    LocalDateTime create_day;
}
