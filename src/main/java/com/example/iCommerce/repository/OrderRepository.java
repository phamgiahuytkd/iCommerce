package com.example.iCommerce.repository;

import com.example.iCommerce.dto.response.OrderResponse;
import com.example.iCommerce.dto.response.OverviewResponse;
import com.example.iCommerce.entity.Order;
import com.example.iCommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    // Hàm cơ bản
    List<Order> findAllByUserOrderByDateDesc(User user);

    // Hàm nâng cao: lấy đơn theo status logic từ order_status
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
        id, name, amount, address, fulladdress, note, phone, date, status, payment, device, user_id
    FROM final_orders
    WHERE user_id = :userId
      AND status = :status
    ORDER BY latest_update_day DESC
    """, nativeQuery = true)
    Page<Object[]> findOrdersByUserAndStatus(@Param("userId") String userId,
                                             @Param("status") String status,
                                             Pageable pageable);


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
        id, name, amount, address, fulladdress, note, phone, date, status, payment, device, user_id
    FROM final_orders
    WHERE id = :orderId
    """, nativeQuery = true)
    List<Object[]> findOrderWithStatusById(@Param("orderId") String orderId);


    /// admin ///
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
        id, name, amount, address, fulladdress, note, phone, date, status, payment, device, user_id
    FROM final_orders
    WHERE (:status = 'all' OR status = :status)
    ORDER BY latest_update_day DESC
    """, nativeQuery = true)
    Page<Object[]> findAllOrdersByStatus(@Param("status") String status, Pageable pageable);



    // dashboard//
    @Query(value = """
    SELECT
        COUNT(DISTINCT o.id) AS total_orders,
        SUM(DISTINCT o.amount) AS total_revenue,
        ROUND(SUM(DISTINCT o.amount) * 1.0 / COUNT(DISTINCT o.id), 2) AS average_order,
        SUM(c.quantity) AS total_sold_products
    FROM orders o
    JOIN cart c ON c.order_id = o.id
    WHERE EXISTS (
        SELECT 1
        FROM order_status os
        WHERE os.order_id = o.id AND os.status = 'DELIVERED'
    )
    AND EXISTS (
        SELECT 1
        FROM order_status os
        WHERE os.order_id = o.id AND os.status = 'PAID'
    )
    AND (
        (:type = 'WEEK' AND YEAR(o.date) = YEAR(CURDATE()) AND WEEK(o.date, 1) = WEEK(CURDATE(), 1))
        OR (:type = 'MONTH' AND YEAR(o.date) = YEAR(CURDATE()) AND MONTH(o.date) = MONTH(CURDATE()))
        OR (:type = 'YEAR' AND YEAR(o.date) = YEAR(CURDATE()))
        OR (:type = 'ALL')
    )
""", nativeQuery = true)
    List<Object[]> getOverview(@Param("type") String type);

    @Query(value = """
    WITH orders_filtered AS (
        SELECT
            o.id,
            o.amount,
            o.date,
            CASE
                WHEN :type = 'WEEK' THEN
                    CASE DAYOFWEEK(o.date)
                        WHEN 1 THEN 'CN'
                        WHEN 2 THEN 'Th 2'
                        WHEN 3 THEN 'Th 3'
                        WHEN 4 THEN 'Th 4'
                        WHEN 5 THEN 'Th 5'
                        WHEN 6 THEN 'Th 6'
                        WHEN 7 THEN 'Th 7'
                    END
                WHEN :type = 'MONTH' THEN DATE_FORMAT(o.date, '%m-%d')
                WHEN :type = 'YEAR' THEN CONCAT('Tháng ', DATE_FORMAT(o.date, '%m'))
                WHEN :type = 'ALL' THEN DATE_FORMAT(o.date, '%Y')  -- Nhóm theo năm
            END AS time_group
        FROM orders o
        WHERE EXISTS (
            SELECT 1 FROM order_status os1
            WHERE os1.order_id = o.id AND os1.status = 'PAID'
        )
        AND EXISTS (
            SELECT 1 FROM order_status os2
            WHERE os2.order_id = o.id AND os2.status = 'DELIVERED'
        )
        AND (
            (:type = 'WEEK' AND YEARWEEK(o.date, 1) = YEARWEEK(CURDATE(), 1))
            OR (:type = 'MONTH' AND DATE_FORMAT(o.date, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m'))
            OR (:type = 'YEAR' AND YEAR(o.date) = YEAR(CURDATE()))
            OR (:type = 'ALL' AND o.date >= CURDATE() - INTERVAL 4 YEAR) -- Lấy 5 năm gần nhất
        )
    ),
    agg_data AS (
        SELECT time_group, SUM(DISTINCT amount) AS revenue
        FROM orders_filtered
        GROUP BY time_group
    )
    
    SELECT
        time_group,
        revenue
    FROM agg_data
    ORDER BY time_group;
""", nativeQuery = true)
    List<Object[]> getRevenueByDate(@Param("type") String type);

    @Query(value = """
    WITH filtered_orders AS (
        SELECT * FROM orders o
        WHERE 
            (:type = 'ALL'
            OR (:type = 'WEEK' AND o.date >= DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY))
            OR (:type = 'MONTH' AND o.date >= DATE_FORMAT(CURDATE(), '%Y-%m-01'))
            OR (:type = 'YEAR' AND o.date >= DATE_FORMAT(CURDATE(), '%Y-01-01'))
            )
    ),
    top5 AS (
        SELECT 
            cat.id AS id,
            cat.name AS name,
            SUM(DISTINCT o.amount) AS revenue
        FROM filtered_orders o
        JOIN cart c ON c.order_id = o.id
        JOIN product_variant pv ON c.product_variant_id = pv.id
        JOIN product p ON pv.product_id = p.id
        JOIN category cat ON p.category_id = cat.id
        WHERE EXISTS (
            SELECT 1 FROM order_status os1 WHERE os1.order_id = o.id AND os1.status = 'PAID'
        )
        AND EXISTS (
            SELECT 1 FROM order_status os2 WHERE os2.order_id = o.id AND os2.status = 'DELIVERED'
        )
        GROUP BY cat.id, cat.name
        ORDER BY revenue DESC
        LIMIT 5
    ),
    total_revenue_cte AS (
        SELECT 
            SUM(DISTINCT o.amount) AS total_revenue
        FROM filtered_orders o
        WHERE EXISTS (
            SELECT 1 FROM order_status os1 WHERE os1.order_id = o.id AND os1.status = 'PAID'
        )
        AND EXISTS (
            SELECT 1 FROM order_status os2 WHERE os2.order_id = o.id AND os2.status = 'DELIVERED'
        )
    ),
    top5_revenue_cte AS (
        SELECT SUM(revenue) AS top5_revenue FROM top5
    )

    -- Lấy top 5
    SELECT * FROM top5

    UNION ALL

    -- Lấy mục "Khác" nếu có
    SELECT 
        NULL AS id,
        'Khác' AS name,
        (tr.total_revenue - t5.top5_revenue) AS revenue
    FROM total_revenue_cte tr, top5_revenue_cte t5
    WHERE (tr.total_revenue - t5.top5_revenue) > 0
""", nativeQuery = true)
    List<Object[]> getRevenueByCategory(@Param("type") String type);


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
    AND (
        (:type = 'WEEK' AND YEARWEEK(o.date, 1) = YEARWEEK(CURDATE(), 1))
        OR (:type = 'MONTH' AND DATE_FORMAT(o.date, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m'))
        OR (:type = 'YEAR' AND YEAR(o.date) = YEAR(CURDATE()))
        OR (:type = 'ALL')
    )
    GROUP BY p.id, p.image, p.name
    ORDER BY sales DESC
    LIMIT 10
""", nativeQuery = true)
    List<Object[]> getTopSellingProducts(@Param("type") String type);


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
    WHERE (:type = 'ALL' OR o.date >= 
      CASE 
        WHEN :type = 'WEEK' THEN DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY)
        WHEN :type = 'MONTH' THEN DATE_FORMAT(CURDATE(), '%Y-%m-01')
        WHEN :type = 'YEAR' THEN DATE_FORMAT(CURDATE(), '%Y-01-01')
      END
    )
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
    List<Object[]> findTopGiftSelected(@Param("type") String type);




}
