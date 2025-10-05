package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoggedResponse {
    String full_name;
    String avatar;
    String default_address;
    String account_type;
    Integer reputation;
}
