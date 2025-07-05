package com.example.iCommerce.repository;

import com.example.iCommerce.dto.response.OrderResponse;
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


}
