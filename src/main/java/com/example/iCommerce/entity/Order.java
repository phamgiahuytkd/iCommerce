package com.example.iCommerce.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    Long amount;
    @Column(columnDefinition = "TEXT")
    String address;
    @Column(columnDefinition = "TEXT")
    String fulladdress;
    @Column(columnDefinition = "TEXT")
    String note;
    String phone;
    LocalDateTime date;
    String payment;
    String device;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Khai báo khóa ngoại product_id liên kết với bảng Products
    User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "voucher_id", nullable = true) // Khai báo khóa ngoại product_id liên kết với bảng Products
    Voucher voucher;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Cart> carts;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Rating> ratings;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderStatus> orderStatuses;
}
