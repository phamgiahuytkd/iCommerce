package com.example.iCommerce.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

@JsonIgnoreProperties({"carts", "histories"})
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    Long price;
    String brand;
    String colour;
    String image;
    String created_by;
    LocalDateTime created_date;
    int stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductHistory> histories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Cart> carts;
}
