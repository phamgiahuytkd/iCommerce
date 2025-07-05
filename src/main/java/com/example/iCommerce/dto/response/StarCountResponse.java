package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StarCountResponse {
    Integer star; // sao: 1.0 -> 5.0
    Long count;
}
