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
@Table(name = "product_history")
public class ProductHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    Long price;
    String created_by;
    LocalDateTime created_date;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false) // Khai báo khóa ngoại product_id liên kết với bảng Products
    Products product;

}
