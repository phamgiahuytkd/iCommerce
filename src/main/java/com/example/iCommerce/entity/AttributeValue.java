package com.example.iCommerce.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class AttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    @ManyToOne
    @JoinColumn(name = "attribute_id", nullable = false) // Khai báo khóa ngoại product_id liên kết với bảng Products
    Attribute attribute;

    @OneToMany(mappedBy = "attributeValue", cascade = CascadeType.ALL, orphanRemoval = true)
    List<VariantAttribute> variantAttributes;
}
