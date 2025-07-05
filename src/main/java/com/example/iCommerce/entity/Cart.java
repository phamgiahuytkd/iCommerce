package com.example.iCommerce.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"carts", "histories"})
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne// Map với productId trong khóa chính
    @JoinColumn(name = "product_variant_id", nullable = false)
    ProductVariant productVariant;

    @ManyToOne
    @JoinColumn(name = "selected_gift_id")
    Gift selectedGift; // Một chiều từ Cart → Gift là đủ

    @ManyToOne// Map với userId trong khóa chính
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne// Map với userId trong khóa chính
    @JoinColumn(name = "order_id", nullable = true)
    Order order;

    private Long price;
    private Long quantity;
    private String status;

}
