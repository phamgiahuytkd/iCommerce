package com.example.iCommerce.dto.request;

import com.example.iCommerce.validator.NotNullConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductsCreationRequest {
    @NotNullConstraint(notNull = true, notEmptyString = true, message = "NOT_VALUE")
    String name;
    @NotNullConstraint(notNull = true, notEmptyString = false, message = "NOT_VALUE")
    Long price;
    @NotNullConstraint(notNull = true, notEmptyString = true, message = "NOT_VALUE")
    String brand;
    String colour;
    String image;
    int stock;
}
