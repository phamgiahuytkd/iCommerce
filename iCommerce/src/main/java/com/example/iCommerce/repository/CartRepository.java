package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Cart;
import com.example.iCommerce.entity.ProductHistory;
import com.example.iCommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    List<Cart> findAllByUserId(String customer_id);

}
