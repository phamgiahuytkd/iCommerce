package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Cart;
import com.example.iCommerce.entity.ProductHistory;
import com.example.iCommerce.entity.Products;
import com.example.iCommerce.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    @Query("SELECT c FROM Cart c WHERE c.user.id = :customer_id AND c.status = 'WAIT'")
    List<Cart> findAllByUserId(String customer_id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.product.id = :product_id")
    void deleteAllByProductId(@Param("product_id") String product_id);


}
