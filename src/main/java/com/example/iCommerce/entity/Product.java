    package com.example.iCommerce.entity;


    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        String id;
        @Column(columnDefinition = "TEXT")
        String name;
        String image;
        @Column(columnDefinition = "TEXT")
        String description;
        @Column(columnDefinition = "TEXT")
        String instruction;
        @Column(columnDefinition = "TEXT")
        String ingredient;
        Long view;
        @ManyToOne
        @JoinColumn(name = "category_id", nullable = false) // Khai báo khóa ngoại product_id liên kết với bảng Products
        Category category;
        @ManyToOne
        @JoinColumn(name = "brand_id", nullable = false) // Khai báo khóa ngoại product_id liên kết với bảng Products
        Brand brand;

        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
        List<ProductVariant> productVariants;


    }


