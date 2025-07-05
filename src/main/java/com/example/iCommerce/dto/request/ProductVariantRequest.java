package com.example.iCommerce.dto.request;

import com.example.iCommerce.entity.AttributeValue;
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
public class ProductVariantRequest {
    Long price;
    Long stock;
    LocalDateTime stop_day;
    List<String> attributes;
    DiscountRequest discount;
    MultipartFile image;
    List<MultipartFile> images;
}
