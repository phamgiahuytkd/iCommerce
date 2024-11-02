package com.example.iCommerce.repository;

import com.example.iCommerce.entity.OrderHistory;
import com.example.iCommerce.entity.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, String> {
}
