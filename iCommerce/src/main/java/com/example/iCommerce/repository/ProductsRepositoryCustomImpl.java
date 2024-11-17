package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Products;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Slf4j
@Repository
public class ProductsRepositoryCustomImpl implements ProductsRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Products> findByDynamicQuery(String name, String brand, String colour) {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM products p WHERE ");

        // Điều kiện cho 'name'
        if (name != null) {
            queryBuilder.append("p.name LIKE CONCAT('%', :name, '%') COLLATE utf8mb4_unicode_ci");
        } else {
            queryBuilder.append("(p.name IS NULL OR p.name IS NOT NULL)");
        }

        // Điều kiện cho 'brand'
        if (brand != null) {
            queryBuilder.append(" AND p.brand LIKE CONCAT('%', :brand, '%') COLLATE utf8mb4_unicode_ci");
        } else {
            queryBuilder.append(" AND (p.brand IS NULL OR p.brand IS NOT NULL)");
        }

        // Điều kiện cho 'colour'
        if (colour != null) {
            queryBuilder.append(" AND p.colour LIKE CONCAT('%', :colour, '%') COLLATE utf8mb4_unicode_ci");
        } else {
            queryBuilder.append(" AND (p.colour IS NULL OR p.colour IS NOT NULL)");
        }

        // Tạo và thực thi truy vấn native
        Query query = entityManager.createNativeQuery(queryBuilder.toString(), Products.class);

        if (name != null) {
            query.setParameter("name", name);
        }

        if (brand != null) {
            query.setParameter("brand", brand);
        }

        if (colour != null) {
            query.setParameter("colour", colour);
        }


        return query.getResultList();
    }
}

