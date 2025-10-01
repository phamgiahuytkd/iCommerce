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
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String code;
    String description;
    String voucher_type;
    Integer percent;
    Long max_amount;
    Long min_order_amount;
    LocalDateTime start_day;
    LocalDateTime end_day;
    Integer usage_limit;
    Integer used_count;
    @OneToMany(mappedBy = "voucher", cascade = CascadeType.PERSIST)
    List<Order> orders;

}
