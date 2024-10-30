package com.example.iCommerce.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    long price;
    String brand;
    String colour;
    String image;
    String created_by;
    LocalDateTime created_date;
    int stock;

}
