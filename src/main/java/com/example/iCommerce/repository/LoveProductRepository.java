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
    LEFT JOIN product_variant_gift pvg ON pvg.product_variant_id = pv.id
    LEFT JOIN gift g ON g.id = pvg.gift_id AND g.start_day <= NOW() AND g.end_day > NOW()
    LEFT JOIN product_variant pv_gift ON g.product_variant_id = pv_gift.id
    LEFT JOIN product gp ON pv_gift.product_id = gp.id
    LEFT JOIN rating r ON r.product_variant_id = pv.id
    LEFT JOIN brand b ON p.brand_id = b.id
    LEFT JOIN category c ON p.category_id = c.id
    LEFT JOIN (
        SELECT 
            pvg.product_variant_id,
            MAX(pv2.price) AS max_price
        FROM product_variant_gift pvg
        JOIN gift g2 ON g2.id = pvg.gift_id
        JOIN product_variant pv2 ON g2.product_variant_id = pv2.id
        WHERE g2.start_day <= NOW() AND g2.end_day > NOW()
        GROUP BY pvg.product_variant_id
    ) max_gift 
        ON max_gift.product_variant_id = pv.id 
        AND pv_gift.price = max_gift.max_price
    WHERE lp.user_id = :userId
    GROUP BY
        p.id, p.name, b.name, c.name, p.image, p.view,
        p.description, p.instruction, p.ingredient,
        gp.name, pv_gift.image, g.stock, g.start_day, g.end_day, g.id
    ORDER BY lp.create_day DESC
""", nativeQuery = true)
    Page<Object[]> findLovedProductsByUserId(Pageable pageable, @Param("userId") String userId);

    boolean  existsByUserAndProduct(User user, Product product);

    Optional<LoveProduct> findByUserAndProduct(User user, Product product);


}
