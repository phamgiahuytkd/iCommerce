package com.example.iCommerce.repository;

import com.example.iCommerce.dto.response.PriceRangeResponse;
import com.example.iCommerce.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    @Query(value = """
        SELECT\s
            pv.id AS id,
            p.name AS name,
            JSON_ARRAYAGG(
                JSON_OBJECT(
                    'id', av.name,
                    'attribute_id', a.name
                )
            ) AS attribute_values,
            COALESCE(
                MIN(CASE\s
                    WHEN d.end_day > CURRENT_TIMESTAMP AND d.start_day <= CURRENT_TIMESTAMP\s
                    THEN pv.price * (1 - d.percent / 100.0)\s
                    ELSE pv.price\s
                END),
                pv.price
            ) AS price,
            pv.description AS description,
            pv.instruction AS instruction,
            pv.ingredient AS ingredient,
            pv.image AS image,
            pv.images AS images,
            pv.stock AS stock,
            pv.create_day AS create_day,
            pv.stop_day AS stop_day,
            MAX(CASE\s
                WHEN d.end_day > CURRENT_TIMESTAMP AND d.start_day <= CURRENT_TIMESTAMP\s
                THEN d.percent\s
            END) AS percent,
            MIN(CASE\s
                WHEN d.end_day > CURRENT_TIMESTAMP AND d.start_day <= CURRENT_TIMESTAMP\s
                THEN d.start_day\s
            END) AS start_day,
            MAX(CASE\s
                WHEN d.end_day > CURRENT_TIMESTAMP AND d.start_day <= CURRENT_TIMESTAMP\s
                THEN d.end_day\s
            END) AS end_day,
            AVG(r.star) AS star,
            COUNT(DISTINCT CONCAT(r.product_variant_id, '_', r.user_id, '_', r.create_day)) AS rating_quantity
        FROM\s
            product_variant pv
            JOIN product p ON p.id = pv.product_id
            LEFT JOIN discount d ON d.product_variant_id = pv.id\s
                AND d.end_day > CURRENT_TIMESTAMP\s
                AND d.start_day <= CURRENT_TIMESTAMP
            LEFT JOIN variant_attribute va ON va.product_variant_id = pv.id
            LEFT JOIN attribute_value av ON av.id = va.attribute_value_id
            LEFT JOIN attribute a ON a.id = av.attribute_id
            LEFT JOIN rating r ON r.product_variant_id = pv.id
        WHERE\s
            pv.product_id = :productId
        GROUP BY\s
            pv.id,
            p.name,
            pv.description,
            pv.instruction,
            pv.ingredient,
            pv.image,
            pv.images,
            pv.stock,
            pv.create_day,
            pv.stop_day,
            pv.price
        ORDER BY\s
            pv.create_day DESC
       \s""", nativeQuery = true)
    List<Object[]> findProductVariantsByProductId(@Param("productId") String productId);

    @Query("SELECT new com.example.iCommerce.dto.response.PriceRangeResponse(MIN(pv.price), MAX(pv.price)) FROM ProductVariant pv")
    PriceRangeResponse findPriceRange();
}