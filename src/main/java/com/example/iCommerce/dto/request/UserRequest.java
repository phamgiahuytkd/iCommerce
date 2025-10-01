package com.example.iCommerce.dto.request;

import com.example.iCommerce.validator.NotNullConstraint;
import com.example.iCommerce.validator.PasswordConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    @NotNullConstraint(notNull = true, notEmptyString = true, message = "NOT_VALUE")
    String email;
    @PasswordConstraint(min=6, containSpecialChar = false, containUpperChar = true, message = "PASSWORD_INVALID")
    String password;
    String full_name;
    String phone;
    String default_shipping_address;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate date_of_birth;
    MultipartFile avatar;
}
