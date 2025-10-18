package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.Order;
import com.example.iCommerce.entity.OrderStatus;
import com.example.iCommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, String> {
    List<OrderStatus> findAllByOrder(Order order);
    boolean existsByOrderAndStatus(Order order, String status);


}
