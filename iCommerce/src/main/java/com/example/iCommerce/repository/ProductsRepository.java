package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Products;
import com.example.iCommerce.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductsRepository extends JpaRepository<Products, String>, ProductsRepositoryCustom {
    boolean existsByNameAndBrand(String name, String brand);


    List<Products> findByBrandAndName(String brand, String name);
}
