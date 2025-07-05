package com.example.iCommerce.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@IdClass(VariantAttributeId.class)
public class VariantAttribute {

    @Id
    @ManyToOne
    @JoinColumn(name = "product_variant_id", nullable = false) // Khai báo khóa ngoại product_id liên kết với bảng Products
    ProductVariant productVariant;


    @Id
    @ManyToOne
    @JoinColumn(name = "attribute_value_id", nullable = false)
    AttributeValue attributeValue;

}
