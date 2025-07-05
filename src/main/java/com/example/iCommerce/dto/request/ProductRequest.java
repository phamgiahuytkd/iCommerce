package com.example.iCommerce.dto.request;

import com.example.iCommerce.validator.NotNullConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    String name;
    String category_id;
    String brand_id;
    MultipartFile image;
    String description;
    String ingredient;
    String instruction;
    List<ProductVariantRequest> variants;
}
