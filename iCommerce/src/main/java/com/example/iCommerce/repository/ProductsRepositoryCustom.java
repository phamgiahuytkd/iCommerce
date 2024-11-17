package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Products;

import java.util.List;

public interface ProductsRepositoryCustom {
    List<Products> findByDynamicQuery(String name, String brand, String colour);
}
