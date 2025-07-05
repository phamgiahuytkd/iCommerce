package com.example.iCommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRatingResponse {
    String product_variant_id;
    String name;
    List<AttributeValueResponse> attribute_values;
    String image;
    String full_name;
    LocalDateTime create_day;
    Integer star;
    String images;
    String comment;

}
