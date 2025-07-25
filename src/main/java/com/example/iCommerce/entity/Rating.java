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
@IdClass(RatingId.class)
public class Rating {

    @Id
    @ManyToOne
    @JoinColumn(name = "product_variant_id", nullable = false) // Khai báo khóa ngoại product_id liên kết với bảng Products
    ProductVariant productVariant;
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Khai báo khóa ngoại product_id liên kết với bảng Products
    User user;
    @Id
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Order order;
    Double star;
    String images;
    @Column(columnDefinition = "TEXT")
    String comment;
    LocalDateTime create_day;

}
