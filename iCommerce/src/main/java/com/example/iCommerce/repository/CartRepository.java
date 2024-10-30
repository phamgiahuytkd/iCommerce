package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Cart;
import com.example.iCommerce.entity.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
}
