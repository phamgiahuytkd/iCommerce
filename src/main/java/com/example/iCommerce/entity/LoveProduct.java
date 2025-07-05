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
@IdClass(LoveProductId.class)
public class LoveProduct {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    LocalDateTime create_day;
}
