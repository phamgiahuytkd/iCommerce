package com.example.iCommerce.repository;

import com.example.iCommerce.entity.ProductHistory;
import com.example.iCommerce.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductHistoryRepository extends JpaRepository<ProductHistory, String> {
}
