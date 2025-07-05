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
public class Poster {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String image;
    String link;
}
