package com.example.iCommerce.repository;

import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.CategoryResponse;
import com.example.iCommerce.entity.Category;
import com.example.iCommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query("""
    SELECT new com.example.iCommerce.dto.response.CategoryResponse(
        c.id, 
        c.name, 
        c.image, 
        COUNT(p)
    )
    FROM Category c
    LEFT JOIN c.products p
    GROUP BY c.id, c.name, c.image
""")
    List<CategoryResponse> findAllWithProductCount();
}
