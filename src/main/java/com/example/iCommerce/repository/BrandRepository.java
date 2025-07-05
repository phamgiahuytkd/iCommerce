package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Brand;
import com.example.iCommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {
}
