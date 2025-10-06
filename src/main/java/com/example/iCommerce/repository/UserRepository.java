package com.example.iCommerce.repository;

import com.example.iCommerce.entity.InvalidatedToken;
import com.example.iCommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);


    @Query(
            value = """
        SELECT
            u.id AS user_id,
            u.full_name AS name,
            u.phone,
            u.email,
            COUNT(DISTINCT f.id) AS order_placed,
            COALESCE(SUM(DISTINCT f.amount), 0) AS expend,
            CASE
                WHEN DATEDIFF(NOW(), u.create_day) < 30 AND u.stop_day IS NULL THEN 'NEW'
                WHEN u.stop_day IS NOT NULL THEN 'BLOCK'
                WHEN DATEDIFF(NOW(), u.create_day) >= 30 AND u.stop_day IS NULL THEN 'NORMAL'
                ELSE 'UNKNOWN'
            END AS status,
            u.reputation,
            MAX(f.date) AS latest_order_date
        FROM `user` u
        LEFT JOIN (
            SELECT o.id, o.user_id, o.amount, o.date
            FROM orders o
            JOIN order_status os1 ON os1.order_id = o.id AND os1.status = 'PAID'
            JOIN order_status os2 ON os2.order_id = o.id AND os2.status = 'DELIVERED'
        ) f ON f.user_id = u.id
        WHERE u.user_type != 'ADMIN'
        GROUP BY u.id, u.full_name, u.phone, u.email, u.create_day, u.stop_day, u.reputation
        ORDER BY u.stop_day IS NOT NULL, MAX(f.date) DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM `user` u
        WHERE u.user_type != 'ADMIN'
        """,
            nativeQuery = true
    )
    Page<Object[]> findAllCustomer(Pageable pageable);



    /// Customer ///
    @Query(value = """
        WITH status_flags AS (
            SELECT
                o.id AS order_id,
                o.amount,
                MAX(CASE WHEN os.status = 'PROCESSING' THEN 1 ELSE 0 END) AS has_processing,
                MAX(CASE WHEN os.status = 'DELIVERED' THEN 1 ELSE 0 END) AS has_delivered,
                MAX(CASE WHEN os.status = 'PAID' THEN 1 ELSE 0 END) AS has_paid,
                MAX(CASE WHEN os.status = 'CANCELED' THEN 1 ELSE 0 END) AS has_canceled,
                MAX(CASE WHEN os.status = 'REFUSED' THEN 1 ELSE 0 END) AS has_refused,
                MIN(CASE WHEN os.status = 'DELIVERED' THEN os.update_day END) AS delivered_day,
                MIN(CASE WHEN os.status = 'PAID' THEN os.update_day END) AS paid_day
            FROM orders o
            JOIN order_status os ON o.id = os.order_id
            WHERE o.user_id = :userId
            GROUP BY o.id, o.amount
        )
        SELECT
            COALESCE(SUM(CASE WHEN has_delivered = 1 AND has_paid = 1 THEN amount ELSE 0 END), 0),
            COALESCE(COUNT(*), 0),
            COALESCE(SUM(CASE WHEN has_processing = 1 AND has_delivered = 0 AND has_paid = 0 
                                   AND has_canceled = 0 AND has_refused = 0
                              THEN 1 ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN has_delivered = 1 AND has_paid = 1 THEN 1 ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN has_canceled = 1 OR has_refused = 1 THEN 1 ELSE 0 END), 0),
            COALESCE(SUM(CASE 
                            WHEN has_delivered = 1 AND (
                                    paid_day IS NULL 
                                    OR paid_day > DATE_ADD(delivered_day, INTERVAL 1 DAY)
                                )
                            THEN 1 ELSE 0 END), 0)
        FROM status_flags
        """, nativeQuery = true)
    List<Object[]> getUserOverview(@Param("userId") String userId);

    @Query(value = """
        WITH ranked_status AS (
            SELECT *,
                   ROW_NUMBER() OVER (PARTITION BY order_id ORDER BY update_day DESC) AS rn
            FROM order_status
        ),
        latest_status AS (
            SELECT * FROM ranked_status WHERE rn = 1
        ),
        second_latest_status AS (
            SELECT * FROM ranked_status WHERE rn = 2
        ),
        orders_with_status AS (
            SELECT
                o.*,
                latest.status AS latest_status,
                second.status AS second_latest_status,
                latest.update_day AS latest_update_day,
                EXISTS (
                    SELECT 1 FROM order_status os2
                    WHERE os2.order_id = o.id AND os2.status = 'DELIVERED'
                ) AS has_delivered,
                EXISTS (
                    SELECT 1 FROM order_status os3
                    WHERE os3.order_id = o.id AND os3.status = 'PAID'
                ) AS has_paid
            FROM orders o
            LEFT JOIN latest_status latest ON o.id = latest.order_id
            LEFT JOIN second_latest_status second ON o.id = second.order_id
            WHERE o.user_id = :userId
        ),
        final_orders AS (
            SELECT
                *,
                CASE
                    WHEN latest_status IN ('REFUSED', 'CANCELED') THEN 'UNCOMPLETED'
                    WHEN latest_status = 'APPROVED' THEN 'PROCESSING'
                    WHEN latest_status = 'PAID' THEN
                        CASE
                            WHEN has_delivered THEN 'COMPLETED'
                            ELSE COALESCE(second_latest_status, 'PAID')
                        END
                    WHEN latest_status = 'DELIVERED' THEN
                        CASE
                            WHEN has_paid THEN 'COMPLETED'
                            ELSE 'PENDING'
                        END
                    ELSE latest_status
                END AS status
            FROM orders_with_status
        )
        SELECT
            id,
            name,
            amount,
            address,
            fulladdress,
            note,
            phone,
            date,
            status,
            payment,
            device,
            user_id
        FROM final_orders
        ORDER BY latest_update_day DESC
        """,
            nativeQuery = true)
    List<Object[]> findAllOrdersByUserId(@Param("userId") String userId);

    @Query(value = """
    SELECT 
        p.id AS id,
        p.image AS image,
        p.name AS name,
        SUM(c.quantity) AS sales,
        SUM(c.quantity * pv.price) AS revenue,
        SUM(pv.stock) AS stock
    FROM orders o
    JOIN cart c ON c.order_id = o.id
    JOIN product_variant pv ON c.product_variant_id = pv.id
    JOIN product p ON pv.product_id = p.id
    WHERE EXISTS (
        SELECT 1 FROM order_status os1 
        WHERE os1.order_id = o.id AND os1.status = 'PAID'
    )
    AND EXISTS (
        SELECT 1 FROM order_status os2 
        WHERE os2.order_id = o.id AND os2.status = 'DELIVERED'
    )
    AND o.user_id = :userId
    GROUP BY p.id, p.image, p.name
    ORDER BY sales DESC
    LIMIT 10
    """, nativeQuery = true)
    List<Object[]> findTopProductSelectedByUser(@Param("userId") String userId);

    @Query(value = """
    SELECT
      g.id AS gift_id,
      pv.id AS variant_id,
      p.name AS product_name,
      pv.image AS image,
      (
        SELECT JSON_ARRAYAGG(
                 JSON_OBJECT(
                   'id', av2.name,
                   'attribute_id', a2.name
                 )
               )
        FROM (
          SELECT DISTINCT av.name, a.name AS attribute_name
          FROM variant_attribute va2
          JOIN attribute_value av ON va2.attribute_value_id = av.id
          JOIN attribute a ON a.id = av.attribute_id
          WHERE va2.product_variant_id = pv.id
        ) AS distinct_attrs(av_name, attr_name)
        JOIN attribute_value av2 ON av2.name = av_name
        JOIN attribute a2 ON a2.name = attr_name
      ) AS attribute_values,
      g.stock,
      g.start_day,
      g.end_day,
      COUNT(DISTINCT c.id) AS total_selected_times
    FROM gift g
    JOIN product_variant pv ON g.product_variant_id = pv.id
    JOIN product p ON pv.product_id = p.id
    LEFT JOIN cart c ON c.selected_gift_id = g.id
    LEFT JOIN orders o ON o.id = c.order_id
    WHERE o.user_id = :userId
      AND EXISTS (
          SELECT 1 FROM order_status os1 
          WHERE os1.order_id = o.id AND os1.status = 'PAID'
      )
      AND EXISTS (
          SELECT 1 FROM order_status os2 
          WHERE os2.order_id = o.id AND os2.status = 'DELIVERED'
      )
    GROUP BY g.id, pv.id, p.name, pv.image, g.stock, g.start_day, g.end_day
    ORDER BY total_selected_times DESC
    LIMIT 10
    """, nativeQuery = true)
    List<Object[]> findTopGiftSelectedByUser(@Param("userId") String userId);

}
