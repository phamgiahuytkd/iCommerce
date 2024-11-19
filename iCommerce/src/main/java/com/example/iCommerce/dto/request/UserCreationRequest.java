package com.example.iCommerce.dto.request;

import com.example.iCommerce.validator.NotNullConstraint;
import com.example.iCommerce.validator.PasswordConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotNullConstraint(notNull = true, notEmptyString = true, message = "NOT_VALUE")
    String email;
    @PasswordConstraint(min=9, containSpecialChar = true, containUpperChar = true, message = "PASSWORD_INVALID")
    String password;
    @NotNullConstraint(notNull = true, notEmptyString = true, message = "NOT_VALUE")
    String full_name;
    String phone;
    String default_shipping_address;
    String social_type;
    String social_key;
}
