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
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    Long price;
    String status;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false) // Khai báo khóa ngoại product_id liên kết với bảng Products
    Products product;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false) // Khai báo khóa ngoại product_id liên kết với bảng Products
    User user;




}
