package com.example.iCommerce.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@IdClass(DiscountId.class)
public class Discount {

    @Id
    @ManyToOne
    @JoinColumn(name = "product_variant_id", nullable = false) // Khai báo khóa ngoại product_id liên kết với bảng Products
    ProductVariant productVariant;
    Integer percent;
    @Id
    LocalDateTime start_day;
    LocalDateTime end_day;

}
