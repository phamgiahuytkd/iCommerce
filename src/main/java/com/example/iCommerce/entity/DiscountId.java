package com.example.iCommerce.entity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable // Đánh dấu là khóa chính hỗn hợp
public class DiscountId implements Serializable {
    private String productVariant;
    private LocalDateTime start_day;
}
