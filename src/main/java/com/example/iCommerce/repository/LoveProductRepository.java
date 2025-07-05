package com.example.iCommerce.repository;

import com.example.iCommerce.dto.response.ProductResponse;
import com.example.iCommerce.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoveProductRepository extends JpaRepository<LoveProduct, LoveProductId> {

    @Query(value = """
    SELECT
        p.id,
        p.name,
        COALESCE(
            MIN(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN pv.price * (1 - d.percent / 100.0) END),
            MIN(pv.price)
        ) AS price,
        b.name AS brand,
        c.name AS category,
        p.image,
        p.view,
        SUM(pv.stock) AS stock,
        MAX(pv.create_day) AS create_day,
        MAX(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN d.percent END) AS percent,
        gp.name AS gift_name,
        pv_gift.image AS gift_image,
        g.stock AS gift_stock,
        g.start_day AS gift_start_day,
        g.end_day AS gift_end_day,
        AVG(r.star) AS star,
        g.product_variant_id AS gift_id
    FROM love_product lp
    JOIN product p ON p.id = lp.product_id
    LEFT JOIN product_variant pv ON pv.product_id = p.id
    LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW()
    LEFT JOIN gift g ON p.gift_id = g.id AND g.end_day > NOW() AND g.start_day <= NOW()
    LEFT JOIN product_variant pv_gift ON g.product_variant_id = pv_gift.id
    LEFT JOIN product gp ON pv_gift.product_id = gp.id
    LEFT JOIN rating r ON r.product_variant_id = pv.id
    LEFT JOIN brand b ON p.brand_id = b.id
    LEFT JOIN category c ON p.category_id = c.id
    WHERE lp.user_id = :userId
    GROUP BY
        p.id, p.name, b.name, c.name, p.image, p.view,
        gp.name, pv_gift.image, g.stock, g.start_day, g.end_day, g.id
    ORDER BY lp.create_day DESC
    """, nativeQuery = true)
    Page<Object[]> findLovedProductsByUserId(Pageable pageable, @Param("userId") String userId);

    boolean  existsByUserAndProduct(User user, Product product);

    Optional<LoveProduct> findByUserAndProduct(User user, Product product);


}
