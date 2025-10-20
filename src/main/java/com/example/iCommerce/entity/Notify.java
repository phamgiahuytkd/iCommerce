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
public class Notify {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String type;
    String type_id;
    String title;
    String message;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    LocalDateTime create_day;
}
