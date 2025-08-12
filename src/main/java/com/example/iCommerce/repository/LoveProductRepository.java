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
    WITH best_gift_per_product AS (
        SELECT 
            p.id AS product_id,
            g.product_variant_id AS gift_id,
            pv_gift.price AS gift_price,
            pv_gift.image AS gift_image,
            g.stock AS gift_stock,
            g.start_day,
            g.end_day,
            gp.name AS gift_name,
            ROW_NUMBER() OVER (PARTITION BY p.id ORDER BY pv_gift.price DESC) AS rn
        FROM product p
        JOIN product_variant pv ON pv.product_id = p.id
        JOIN product_variant_gift pvg ON pvg.product_variant_id = pv.id
        JOIN gift g ON g.id = pvg.gift_id AND g.start_day <= NOW() AND g.end_day > NOW()
        JOIN product_variant pv_gift ON pv_gift.id = g.product_variant_id
        JOIN product gp ON gp.id = pv_gift.product_id
    )

    SELECT
        p.id,
        p.name,
        COALESCE(
            MIN(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() 
                     THEN pv.price * (1 - d.percent / 100.0) 
            END),
            MIN(pv.price)
        ) AS price,
        b.name AS brand,
        c.name AS category,
        p.image,
        p.view,
        p.description,
        p.instruction,
        p.ingredient,
        SUM(pv.stock) AS stock,
        MAX(pv.create_day) AS create_day,
        MAX(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN d.percent END) AS percent,
        bg.gift_name,
        bg.gift_image,
        bg.gift_stock,
        bg.start_day AS gift_start_day,
        bg.end_day AS gift_end_day,
        AVG(r.star) AS star,
        bg.gift_id
    FROM love_product lp
    JOIN product p ON p.id = lp.product_id
    LEFT JOIN brand b ON p.brand_id = b.id
    LEFT JOIN category c ON p.category_id = c.id
    LEFT JOIN product_variant pv ON pv.product_id = p.id
    LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW()
    LEFT JOIN rating r ON r.product_variant_id = pv.id
    LEFT JOIN best_gift_per_product bg ON bg.product_id = p.id AND bg.rn = 1

    WHERE lp.user_id = :userId

    GROUP BY
        p.id, p.name, b.name, c.name, p.image, p.view,
        p.description, p.instruction, p.ingredient,
        bg.gift_name, bg.gift_image, bg.gift_stock,
        bg.start_day, bg.end_day, bg.gift_id

    ORDER BY lp.create_day DESC
""", nativeQuery = true)
    Page<Object[]> findLovedProductsByUserId(Pageable pageable, @Param("userId") String userId);

    boolean  existsByUserAndProduct(User user, Product product);

    Optional<LoveProduct> findByUserAndProduct(User user, Product product);


}
