package com.example.iCommerce.dto.request;


import com.example.iCommerce.validator.PasswordConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @PasswordConstraint(min=9, containSpecialChar = true, containUpperChar = true, message = "PASSWORD_INVALID")
    String password;
    String full_name;
    String phone;
    String default_shipping_address;
    String social_type;
    String social_key;
    String user_type;
}
