package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Products;
import com.example.iCommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductsRepository extends JpaRepository<Products, String> {
    boolean existsByNameAndBrand(String name, String brand);

}
