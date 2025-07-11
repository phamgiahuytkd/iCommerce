package com.example.iCommerce.repository;

import com.example.iCommerce.dto.response.GiftResponse;
import com.example.iCommerce.entity.Brand;
import com.example.iCommerce.entity.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftRepository extends JpaRepository<Gift, String> {
    @Query(value = """
    SELECT
        g.id,
        pv.id,
        p.name,
        pv.image,
        JSON_ARRAYAGG(
            JSON_OBJECT(
                'id', av.id,
                'attribute_id', a.id
            )
        ) AS attribute_values
    FROM gift g
    JOIN product_variant pv ON g.product_variant_id = pv.id
    JOIN product p ON pv.product_id = p.id
    LEFT JOIN variant_attribute va ON va.product_variant_id = pv.id
    LEFT JOIN attribute_value av ON va.attribute_value_id = av.id
    LEFT JOIN attribute a ON a.id = av.attribute_id
    WHERE g.start_day <= CURRENT_TIMESTAMP
      AND g.end_day >= CURRENT_TIMESTAMP
      AND g.stock > 0
      AND p.id IN (:productIds)
    GROUP BY g.id, pv.id, p.name, pv.image
""", nativeQuery = true)
    List<Object[]> findValidGiftsByProductIds(@Param("productIds") List<String> productIds);



    @Query(value = """
    SELECT
        g.id,
        gpv.id,
        gp.name,
        gpv.image,
        JSON_ARRAYAGG(
            JSON_OBJECT(
                'id', av.id,
                'attribute_id', a.id
            )
        ) AS attribute_values
    FROM product_variant_gift pg
    JOIN gift g ON g.id = pg.gift_id
    JOIN product_variant gpv ON gpv.id = g.product_variant_id
    JOIN product gp ON gp.id = gpv.product_id
    LEFT JOIN variant_attribute va ON va.product_variant_id = gpv.id
    LEFT JOIN attribute_value av ON va.attribute_value_id = av.id
    LEFT JOIN attribute a ON a.id = av.attribute_id
    WHERE pg.product_variant_id = :productVariantId
      AND g.start_day <= CURRENT_TIMESTAMP
      AND g.end_day >= CURRENT_TIMESTAMP
      AND g.stock > 0
    GROUP BY g.id, gpv.id, gp.name, gpv.image
""", nativeQuery = true)
    List<Object[]> findValidGiftsByProductVariantId(@Param("productVariantId") String productVariantId);

}
