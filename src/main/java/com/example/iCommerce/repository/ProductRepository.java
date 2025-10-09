package com.example.iCommerce.repository;

import com.example.iCommerce.dto.response.PriceRangeResponse;
import com.example.iCommerce.dto.response.ProductResponse;
import com.example.iCommerce.entity.Brand;
import com.example.iCommerce.entity.Category;
import com.example.iCommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, ProductRepositoryCustom  {
    List<Product> findAllByCategoryAndBrand(Category category, Brand brand);

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
        WHERE p.id = :id
    )

    SELECT
        p.id,
        p.name,
        COALESCE(
            MIN(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN pv.price * (1 - d.percent / 100.0) END),
            MIN(pv.price)
        ) AS price,
        b.name,
        c.name,
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
    FROM product p
    LEFT JOIN brand b ON p.brand_id = b.id
    LEFT JOIN category c ON p.category_id = c.id
    LEFT JOIN product_variant pv ON pv.product_id = p.id
    LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW()
    LEFT JOIN rating r ON r.product_variant_id = pv.id
    LEFT JOIN best_gift_per_product bg ON bg.product_id = p.id AND bg.rn = 1
    WHERE p.id = :id
    GROUP BY
        p.id, p.name, b.name, c.name, p.image, p.view,
        p.description, p.instruction, p.ingredient,
        bg.gift_name, bg.gift_image, bg.gift_stock,
        bg.start_day, bg.end_day, bg.gift_id
""", nativeQuery = true)
    List<Object[]> findProductById(@Param("id") String id);


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
            MIN(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN pv.price * (1 - d.percent / 100.0) END),
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
    FROM product p
    LEFT JOIN brand b ON p.brand_id = b.id
    LEFT JOIN category c ON p.category_id = c.id
    LEFT JOIN product_variant pv ON pv.product_id = p.id
    LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW()
    LEFT JOIN rating r ON r.product_variant_id = pv.id
    LEFT JOIN best_gift_per_product bg ON bg.product_id = p.id AND bg.rn = 1
    GROUP BY
        p.id, p.name, b.name, c.name, p.image, p.view,
        p.description, p.instruction, p.ingredient,
        bg.gift_name, bg.gift_image, bg.gift_stock,
        bg.start_day, bg.end_day, bg.gift_id
    ORDER BY MAX(pv.create_day) DESC
    """, nativeQuery = true)
    List<Object[]> findProducts();


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
            MIN(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN pv.price * (1 - d.percent / 100.0) END),
            MIN(pv.price)
        ) AS price,
        b.name AS brand_name,
        c.name AS category_name,
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
    FROM product p
    LEFT JOIN brand b ON p.brand_id = b.id
    LEFT JOIN category c ON p.category_id = c.id
    LEFT JOIN product_variant pv ON pv.product_id = p.id
    LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW()
    LEFT JOIN rating r ON r.product_variant_id = pv.id
    LEFT JOIN best_gift_per_product bg ON bg.product_id = p.id AND bg.rn = 1

    GROUP BY
        p.id, p.name, b.name, c.name, p.image, p.view, p.description, p.instruction, p.ingredient,
        bg.gift_name, bg.gift_image, bg.gift_stock, bg.start_day, bg.end_day, bg.gift_id

    HAVING MAX(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN d.percent END) IS NOT NULL
    ORDER BY percent DESC
""", nativeQuery = true)
    List<Object[]> findProductsDiscount();


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
        b.name AS brand_name,
        c.name AS category_name,
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
    FROM product p
    LEFT JOIN brand b ON p.brand_id = b.id
    LEFT JOIN category c ON p.category_id = c.id
    LEFT JOIN product_variant pv ON pv.product_id = p.id
    LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW()
    LEFT JOIN rating r ON r.product_variant_id = pv.id
    LEFT JOIN best_gift_per_product bg ON bg.product_id = p.id AND bg.rn = 1

    GROUP BY
        p.id, p.name, b.name, c.name, p.image, p.view, p.description, p.instruction, p.ingredient,
        bg.gift_name, bg.gift_image, bg.gift_stock, bg.start_day, bg.end_day, bg.gift_id

    HAVING MAX(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN d.percent END) IS NOT NULL

    ORDER BY percent DESC
    """, nativeQuery = true)
    List<Object[]> findTop10ProductDiscount();

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
            MIN(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN pv.price * (1 - d.percent / 100.0) END),
            MIN(pv.price)
        ) AS price,
        b.name AS brand_name,
        c.name AS category_name,
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
    FROM product p
    LEFT JOIN brand b ON p.brand_id = b.id
    LEFT JOIN category c ON p.category_id = c.id
    LEFT JOIN product_variant pv ON pv.product_id = p.id
    LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW()
    LEFT JOIN rating r ON r.product_variant_id = pv.id
    LEFT JOIN best_gift_per_product bg ON bg.product_id = p.id AND bg.rn = 1

    GROUP BY
        p.id, p.name, b.name, c.name, p.image, p.view,
        p.description, p.instruction, p.ingredient,
        bg.gift_name, bg.gift_image, bg.gift_stock,
        bg.start_day, bg.end_day, bg.gift_id

    ORDER BY MAX(pv.create_day) DESC
""", countQuery = "SELECT COUNT(*) FROM product p", nativeQuery = true)
    List<Object[]> findLatestProducts();



    /// admin ///
    @Query(
            value = """
        SELECT 
            p.id,
            p.name,
            b.name AS brand,
            c.name AS category,
            p.image,
            p.description,
            p.instruction,
            p.ingredient,

            -- Trung bình sao
            IFNULL((
                SELECT ROUND(AVG(r.star), 1)
                FROM product_variant pv2
                JOIN rating r ON r.product_variant_id = pv2.id
                WHERE pv2.product_id = p.id
            ), 0) AS star,

            -- Tổng đã bán (chỉ tính đơn hàng PAID và DELIVERED)
            IFNULL((
                SELECT SUM(ca.quantity)
                FROM product_variant pv2
                JOIN cart ca ON ca.product_variant_id = pv2.id
                JOIN orders o ON ca.order_id = o.id
                JOIN order_status os1 ON os1.order_id = o.id AND os1.status = 'PAID'
                JOIN order_status os2 ON os2.order_id = o.id AND os2.status = 'DELIVERED'
                WHERE pv2.product_id = p.id
            ), 0) AS sold,

            -- Tổng comment
            IFNULL((
                SELECT COUNT(*)
                FROM product_variant pv2
                JOIN rating r ON r.product_variant_id = pv2.id
                WHERE pv2.product_id = p.id
                  AND r.comment IS NOT NULL AND TRIM(r.comment) <> ''
            ), 0) AS comment,

            -- Lượt xem
            p.view AS view

        FROM product p
        LEFT JOIN brand b ON p.brand_id = b.id
        LEFT JOIN category c ON p.category_id = c.id
        WHERE p.id = :productId
        LIMIT 1
        """,
            nativeQuery = true
    )
    List<Object[]> getProductAdminById(@Param("productId") String productId);

}