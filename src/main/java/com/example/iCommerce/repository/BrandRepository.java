package com.example.iCommerce.repository;

import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.entity.Brand;
import com.example.iCommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {
    @Query("""
    SELECT new com.example.iCommerce.dto.response.BrandResponse(
        b.id, 
        b.name, 
        b.image, 
        COUNT(p)
    )
    FROM Brand b
    LEFT JOIN b.products p
    GROUP BY b.id, b.name, b.image
""")
    List<BrandResponse> findAllWithProductCount();

}
