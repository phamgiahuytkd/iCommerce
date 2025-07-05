package com.example.iCommerce.entity;

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
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    String phone;
    @Column(columnDefinition = "TEXT")
    String address;
    @Column(columnDefinition = "TEXT")
    String address_detail;
    String locate;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;


}
