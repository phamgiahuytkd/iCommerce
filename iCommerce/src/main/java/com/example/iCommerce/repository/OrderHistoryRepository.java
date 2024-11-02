package com.example.iCommerce.repository;

import com.example.iCommerce.entity.OrderHistory;
import com.example.iCommerce.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, String> {
    @Query(value = "SELECT DISTINCT o FROM Orders o WHERE o.id IN (SELECT oh.order.id FROM OrderHistory oh WHERE oh.created_by = :createdBy)")
    List<Orders> findDistinctOrdersByCreatedBy(@Param("createdBy") String createdBy);
}
