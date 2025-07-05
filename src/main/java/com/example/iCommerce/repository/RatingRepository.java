package com.example.iCommerce.repository;

import com.example.iCommerce.dto.response.RatingResponse;
import com.example.iCommerce.dto.response.StarCountResponse;
import com.example.iCommerce.entity.Cart;
import com.example.iCommerce.entity.ProductVariant;
import com.example.iCommerce.entity.Rating;
import com.example.iCommerce.entity.User;
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
public interface RatingRepository extends JpaRepository<Rating, String> {
    @Query("""
    SELECT new com.example.iCommerce.dto.response.StarCountResponse(CAST(r.star AS integer), COUNT(r))
    FROM Rating r
    WHERE r.productVariant.product.id = :productId
    GROUP BY r.star
    ORDER BY r.star
""")
    List<StarCountResponse> countStarByProductId(@Param("productId") String productId);



    @Query("""
    SELECT new com.example.iCommerce.dto.response.RatingResponse(
        r.productVariant.id,
        r.user.full_name,
        r.create_day,
        CAST(r.star AS integer),
        r.images,
        r.comment
    )
    FROM Rating r
    WHERE r.productVariant.product.id = :productId
    ORDER BY r.create_day DESC
""")
    List<RatingResponse> findAllRatingsByProductId(@Param("productId") String productId);









    @Query(value = """
    SELECT
        pv.id AS id,
        p.name AS name,
        pv.image AS image,
        c.order_id AS order_id,
        JSON_ARRAYAGG(
            JSON_OBJECT(
                'id', av.id,
                'attribute_id', a.id
            )
        ) AS attribute_values
    FROM cart c
    JOIN product_variant pv ON c.product_variant_id = pv.id
    JOIN product p ON pv.product_id = p.id
    LEFT JOIN variant_attribute va ON va.product_variant_id = pv.id
    LEFT JOIN attribute_value av ON va.attribute_value_id = av.id
    LEFT JOIN attribute a ON av.attribute_id = a.id
    JOIN orders o ON c.order_id = o.id
    JOIN order_status os1 ON os1.order_id = o.id AND os1.status = 'PAID'
    JOIN order_status os2 ON os2.order_id = o.id AND os2.status = 'DELIVERED'
    LEFT JOIN rating r ON r.product_variant_id = pv.id AND r.order_id = o.id AND r.user_id = c.user_id
    WHERE
        c.user_id = :userId
        AND c.order_id IS NOT NULL
        AND r.order_id IS NULL
    GROUP BY
        pv.id, p.name, pv.image, c.order_id
    """,
            countQuery = """
        SELECT COUNT(DISTINCT pv.id)
        FROM cart c
        JOIN product_variant pv ON c.product_variant_id = pv.id
        JOIN product p ON pv.product_id = p.id
        JOIN orders o ON c.order_id = o.id
        JOIN order_status os1 ON os1.order_id = o.id AND os1.status = 'PAID'
        JOIN order_status os2 ON os2.order_id = o.id AND os2.status = 'DELIVERED'
        LEFT JOIN rating r ON r.product_variant_id = pv.id AND r.order_id = o.id AND r.user_id = c.user_id
        WHERE
            c.user_id = :userId
            AND c.order_id IS NOT NULL
            AND r.order_id IS NULL
    """,
            nativeQuery = true)
    Page<Object[]> findProductsToRateByUser(@Param("userId") String userId, Pageable pageable);



    /////////////////////////
    @Query(value = """
    SELECT
        pv.id AS product_variant_id,
        p.name AS name,
        JSON_ARRAYAGG(
            JSON_OBJECT(
                'id', av.id,
                'attribute_id', a.id
            )
        ) AS attribute_values,
        pv.image AS image,
        u.full_name AS full_name,
        r.create_day AS create_day,
        r.star AS star,
        r.images AS images,
        r.comment AS comment
    FROM
        rating r
        JOIN product_variant pv ON pv.id = r.product_variant_id
        JOIN product p ON p.id = pv.product_id
        JOIN user u ON u.id = r.user_id
        LEFT JOIN variant_attribute va ON va.product_variant_id = pv.id
        LEFT JOIN attribute_value av ON av.id = va.attribute_value_id
        LEFT JOIN attribute a ON a.id = av.attribute_id
    WHERE
        r.user_id = :userId
    GROUP BY
        pv.id,
        p.name,
        pv.image,
        u.full_name,
        r.create_day,
        r.star,
        r.images,
        r.comment
    ORDER BY
        r.create_day DESC
""", nativeQuery = true)
    Page<Object[]> findAllRatingsByUserId(@Param("userId") String userId, Pageable pageable);

}
