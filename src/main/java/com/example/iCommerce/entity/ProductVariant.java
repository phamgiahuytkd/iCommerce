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
    public class ProductVariant {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        String id;
        Long price;
        String image;
        @Column(columnDefinition = "TEXT")
        String images;
        Long stock;
        LocalDateTime create_day;
        LocalDateTime stop_day;

        @ManyToOne
        @JoinColumn(name = "product_id", nullable = true) // nullable nếu không có quà tặng
        Product product;


        @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
        List<Cart> carts;

        @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
        List<Discount> discounts;

        @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
        List<Rating> ratings;

        @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
        List<VariantAttribute> variantAttributes;

        @ManyToMany
        @JoinTable(
                name = "product_variant_gift",
                joinColumns = @JoinColumn(name = "product_variant_id"),
                inverseJoinColumns = @JoinColumn(name = "gift_id")
        )
        List<Gift> gifts;
    }


