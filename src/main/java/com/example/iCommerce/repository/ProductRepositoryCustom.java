package com.example.iCommerce.repository;

import com.example.iCommerce.dto.response.ProductResponse;
import com.example.iCommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Object[]> findByDynamicQuery(String name, String brand, String category, Long min_price, Long max_price);

    List<Object[]> searchProductsByNameOrBrandOrCategory(String input);
}
