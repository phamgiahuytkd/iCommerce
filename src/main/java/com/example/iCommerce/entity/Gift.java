package com.example.iCommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Gift {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "product_variant_id", nullable = false)
    ProductVariant productVariant;
    // món quà là sản phẩm

    Long stock;
    LocalDateTime start_day;
    LocalDateTime end_day;

    // Được tặng kèm với nhiều sản phẩm
    @ManyToMany(mappedBy = "gifts")
    List<ProductVariant> productVariants;
}

