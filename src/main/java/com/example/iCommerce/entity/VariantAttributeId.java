package com.example.iCommerce.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable // Đánh dấu là khóa chính hỗn hợp
public class VariantAttributeId implements Serializable {
    private String productVariant;    // hoặc userId nếu bạn đặt tên khác
    private String attributeValue ;

}
