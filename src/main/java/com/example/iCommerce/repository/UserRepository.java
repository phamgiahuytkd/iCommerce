package com.example.iCommerce.repository;

import com.example.iCommerce.entity.InvalidatedToken;
import com.example.iCommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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






}
