package com.example.iCommerce.repository;

import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    @Query("SELECT c FROM Cart c WHERE c.user.id = :customer_id AND c.order.id IS NULL")
    List<Cart> findAllByUserId(@Param("customer_id") String customer_id);

    @Query("SELECT c FROM Cart c WHERE c.user = :user AND c.productVariant = :productVariant AND c.order.id IS NULL")
    Optional<Cart> findByUserAndProductVariantWithOrderIdNull(@Param("user") User user, @Param("productVariant") ProductVariant productVariant);

    List<Cart> findAllByUser(User user);

    @Modifying
    @Query("UPDATE Cart c SET c.status = :status WHERE c.user.id = :customer_id AND c.status = 'WAIT'")
    void updateStatusByUserID(@Param("status") String status, @Param("customer_id") String customer_id);

    List<Cart> findByIdIn(List<String> ids);

    long countByIdIn(List<String> ids);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.id = :id AND c.status = 'WAIT'")
    void deleteByIdAndStatusWait(@Param("id") String id);

    @Query("SELECT SUM(c.quantity) FROM Cart c WHERE c.user.id = :customer_id AND c.status = 'WAIT'")
    Long sumQuantityByUserIdAndStatusWait(@Param("customer_id") String customerId);


    //    ////////////////////////////////////////////////////////////////
    @Query(value = """
    SELECT
        c.id,
        pv.id AS product_variant_id,
        p.name,
        CAST(CONCAT('[', GROUP_CONCAT(DISTINCT
            CASE
                WHEN av.id IS NOT NULL
                    THEN JSON_OBJECT(
                        'id', av.name,
                        'attribute_id', a.name
                    )
                ELSE NULL
            END
        ), ']') AS JSON) AS attribute_values,

        COALESCE(
            MIN(
                CASE
                    WHEN d.end_day > NOW() AND d.start_day <= NOW()
                        THEN pv.price * (1 - d.percent / 100.0)
                    ELSE pv.price
                END
            ),
            pv.price
        ) AS price,

        pv.image,
        c.quantity,
        pv.stock,
        p.brand_id,

        MAX(
            CASE
                WHEN d.end_day > NOW() AND d.start_day <= NOW()
                    THEN d.percent
            END
        ) AS percent,

        p.id AS product_id,

        g.id AS gift_id,
        gpv.id AS gift_product_variant_id,
        gp.name AS gift_name,
        gpv.image AS gift_image,

        CAST(
            CONCAT(
                '[',
                GROUP_CONCAT(DISTINCT
                    CASE
                        WHEN gav.id IS NOT NULL AND ga.id IS NOT NULL THEN
                            JSON_OBJECT(
                                'id', gav.name,
                                'attribute_id', ga.name
                            )
                        ELSE NULL
                    END
                ),
                ']'
            ) AS JSON
        ) AS gift_attribute_values

    FROM cart c
    JOIN product_variant pv ON pv.id = c.product_variant_id
    JOIN product p ON p.id = pv.product_id

    LEFT JOIN discount d ON d.product_variant_id = pv.id
        AND d.end_day > NOW()
        AND d.start_day <= NOW()

    LEFT JOIN variant_attribute va ON va.product_variant_id = pv.id
    LEFT JOIN attribute_value av ON av.id = va.attribute_value_id
    LEFT JOIN attribute a ON a.id = av.attribute_id

    LEFT JOIN gift g ON g.id = c.selected_gift_id
        AND g.start_day <= NOW()
        AND g.end_day >= NOW()
        AND g.stock > 0

    LEFT JOIN product_variant gpv ON gpv.id = g.product_variant_id
    LEFT JOIN product gp ON gp.id = gpv.product_id
    LEFT JOIN variant_attribute gva ON gva.product_variant_id = gpv.id
    LEFT JOIN attribute_value gav ON gav.id = gva.attribute_value_id
    LEFT JOIN attribute ga ON ga.id = gav.attribute_id

    WHERE
        c.user_id = :userId
        AND c.order_id IS NULL

    GROUP BY
        c.id,
        pv.id,
        p.name,
        pv.image,
        c.quantity,
        pv.stock,
        p.brand_id,
        pv.price,
        p.id,
        g.id, gpv.id, gp.name, gpv.image

    ORDER BY c.id
    """, nativeQuery = true)
    Page<Object[]> findCartResponsesByUserId(@Param("userId") String userId, Pageable pageable);


    @Modifying
    @Query("DELETE FROM Cart c WHERE c.id = :id AND c.order IS NULL")
    void deleteByIdAndOrderIsNull(String id);


    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId AND c.order IS NULL")
    List<Cart> findByUserIdAndOrderIsNull(@Param("userId") String userId);

    @Query(value = """
    SELECT
        c.id,
        pv.id AS product_variant_id,
        p.name,
        CAST(CONCAT('[', GROUP_CONCAT(DISTINCT
            CASE
                WHEN av.id IS NOT NULL
                    THEN JSON_OBJECT(
                        'id', av.name,
                        'attribute_id', a.name
                    )
                ELSE NULL
            END
        ), ']') AS JSON) AS attribute_values,
        c.price,
        pv.image,
        c.quantity,
        pv.stock,
        p.brand_id,
        MAX(
            CASE
                WHEN d.end_day > NOW() AND d.start_day <= NOW()
                    THEN d.percent
            END
        ) AS percent,
        p.id AS product_id,

        g.id AS gift_id,
        gpv.id AS gift_product_variant_id,
        gp.name AS gift_name,
        gpv.image AS gift_image,
        CAST(
            CONCAT(
                '[',
                GROUP_CONCAT(DISTINCT
                    CASE
                        WHEN gav.id IS NOT NULL AND ga.id IS NOT NULL THEN
                            JSON_OBJECT(
                                'id', gav.name,
                                'attribute_id', ga.name
                            )
                        ELSE NULL
                    END
                ),
                ']'
            ) AS JSON
        ) AS gift_attribute_values

    FROM cart c
    JOIN product_variant pv ON pv.id = c.product_variant_id
    JOIN product p ON p.id = pv.product_id

    LEFT JOIN discount d ON d.product_variant_id = pv.id
        AND d.end_day > NOW()
        AND d.start_day <= NOW()

    LEFT JOIN variant_attribute va ON va.product_variant_id = pv.id
    LEFT JOIN attribute_value av ON av.id = va.attribute_value_id
    LEFT JOIN attribute a ON a.id = av.attribute_id

    LEFT JOIN gift g ON g.id = c.selected_gift_id
    LEFT JOIN product_variant gpv ON gpv.id = g.product_variant_id
    LEFT JOIN product gp ON gp.id = gpv.product_id
    LEFT JOIN variant_attribute gva ON gva.product_variant_id = gpv.id
    LEFT JOIN attribute_value gav ON gav.id = gva.attribute_value_id
    LEFT JOIN attribute ga ON ga.id = gav.attribute_id

    WHERE c.order_id = :orderId

    GROUP BY
        c.id,
        pv.id,
        p.name,
        pv.image,
        c.quantity,
        pv.stock,
        p.brand_id,
        c.price,
        p.id,
        g.id, gpv.id, gp.name, gpv.image

    ORDER BY c.id
    """, nativeQuery = true)
    Page<Object[]> findCartResponsesByUserIdAndOrderId(@Param("orderId") String orderId, Pageable pageable);


    boolean existsBySelectedGift_Id(String giftId);



}
