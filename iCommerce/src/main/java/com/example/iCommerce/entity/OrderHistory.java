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
@Table(name = "order_history")
public class OrderHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String action_key;
    String action_name;
    String created_by;
    LocalDateTime created_date;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false) // Khai báo khóa ngoại product_id liên kết với bảng Products
    Orders order;

}
