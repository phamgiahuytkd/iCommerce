package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Orders;
import com.example.iCommerce.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, String> {

}
