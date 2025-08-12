package com.example.iCommerce.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<Object[]> findByDynamicQuery(Pageable pageable, String name, String brandId, String categoryId, Long minPrice, Long maxPrice) {
        StringBuilder queryBuilder = new StringBuilder("""
        WITH best_gift_per_product AS (
            SELECT 
                p.id AS product_id,
                g.product_variant_id AS gift_id,
                pv_gift.price AS gift_price,
                pv_gift.image AS gift_image,
                g.stock AS gift_stock,
                g.start_day,
                g.end_day,
                gp.name AS gift_name,
                ROW_NUMBER() OVER (PARTITION BY p.id ORDER BY pv_gift.price DESC) AS rn
            FROM product p
            JOIN product_variant pv ON pv.product_id = p.id
            JOIN product_variant_gift pvg ON pvg.product_variant_id = pv.id
            JOIN gift g ON g.id = pvg.gift_id AND g.start_day <= NOW() AND g.end_day > NOW()
            JOIN product_variant pv_gift ON pv_gift.id = g.product_variant_id
            JOIN product gp ON gp.id = pv_gift.product_id
        )
        SELECT
            p.id, p.name,
            COALESCE(
                MIN(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN pv.price * (1 - d.percent / 100.0) END),
                MIN(pv.price)
            ) AS price,
            b.name, c.name, p.image, p.view,
            p.description, p.instruction, p.ingredient,
            SUM(pv.stock) AS stock,
            MAX(pv.create_day) AS create_day,
            MAX(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN d.percent END) AS percent,
            bg.gift_name, bg.gift_image, bg.gift_stock,
            bg.start_day AS gift_start_day, bg.end_day AS gift_end_day,
            ROUND(AVG(r.star), 1) AS star, bg.gift_id
        FROM product p
        LEFT JOIN brand b ON p.brand_id = b.id
        LEFT JOIN category c ON p.category_id = c.id
        LEFT JOIN product_variant pv ON pv.product_id = p.id
        LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW()
        LEFT JOIN rating r ON r.product_variant_id = pv.id
        LEFT JOIN best_gift_per_product bg ON bg.product_id = p.id AND bg.rn = 1
        """);

        List<String> whereConditions = new ArrayList<>();
        if (name != null) whereConditions.add("p.name LIKE CONCAT('%', :name, '%')");
        if (brandId != null) whereConditions.add("p.brand_id = :brand_id");
        if (categoryId != null) whereConditions.add("p.category_id = :category_id");

        if (!whereConditions.isEmpty()) {
            queryBuilder.append(" WHERE ").append(String.join(" AND ", whereConditions));
        }

        queryBuilder.append("""
         GROUP BY p.id, p.name, b.name, c.name, p.image, p.view,
                 p.description, p.instruction, p.ingredient,
                 bg.gift_name, bg.gift_image, bg.gift_stock, bg.start_day, bg.end_day, bg.gift_id
        """);

        List<String> havingConditions = new ArrayList<>();
        if (minPrice != null) havingConditions.add("MAX(pv.price) >= :min_price");
        if (maxPrice != null) havingConditions.add("""
        COALESCE(
            MIN(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW()
                THEN pv.price * (1 - d.percent / 100.0) END),
            MIN(pv.price)
        ) <= :max_price
        """);

        if (!havingConditions.isEmpty()) {
            queryBuilder.append(" HAVING ").append(String.join(" AND ", havingConditions));
        }

        queryBuilder.append(" ORDER BY MAX(pv.create_day) DESC");

        Query query = entityManager.createNativeQuery(queryBuilder.toString());
        if (name != null) query.setParameter("name", name);
        if (brandId != null) query.setParameter("brand_id", brandId);
        if (categoryId != null) query.setParameter("category_id", categoryId);
        if (minPrice != null) query.setParameter("min_price", minPrice);
        if (maxPrice != null) query.setParameter("max_price", maxPrice);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        Query countQuery = entityManager.createNativeQuery("SELECT COUNT(DISTINCT p.id) FROM product p");
        Long total = (long) results.size();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Object[]> searchProductsByNameOrBrandOrCategory(Pageable pageable, String input) {
        StringBuilder queryBuilder = new StringBuilder("""
        WITH best_gift_per_product AS (
            SELECT 
                p.id AS product_id,
                g.product_variant_id AS gift_id,
                pv_gift.price AS gift_price,
                pv_gift.image AS gift_image,
                g.stock AS gift_stock,
                g.start_day,
                g.end_day,
                gp.name AS gift_name,
                ROW_NUMBER() OVER (PARTITION BY p.id ORDER BY pv_gift.price DESC) AS rn
            FROM product p
            JOIN product_variant pv ON pv.product_id = p.id
            JOIN product_variant_gift pvg ON pvg.product_variant_id = pv.id
            JOIN gift g ON g.id = pvg.gift_id AND g.start_day <= NOW() AND g.end_day > NOW()
            JOIN product_variant pv_gift ON pv_gift.id = g.product_variant_id
            JOIN product gp ON gp.id = pv_gift.product_id
        )
        SELECT
            p.id, p.name,
            COALESCE(
                MIN(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN pv.price * (1 - d.percent / 100.0) END),
                MIN(pv.price)
            ) AS price,
            b.name, c.name, p.image, p.view,
            p.description, p.instruction, p.ingredient,
            SUM(pv.stock) AS stock,
            MAX(pv.create_day) AS create_day,
            MAX(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN d.percent END) AS percent,
            bg.gift_name, bg.gift_image, bg.gift_stock,
            bg.start_day AS gift_start_day, bg.end_day AS gift_end_day,
            ROUND(AVG(r.star), 1) AS star, bg.gift_id
        FROM product p
        LEFT JOIN brand b ON p.brand_id = b.id
        LEFT JOIN category c ON p.category_id = c.id
        LEFT JOIN product_variant pv ON pv.product_id = p.id
        LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW()
        LEFT JOIN rating r ON r.product_variant_id = pv.id
        LEFT JOIN best_gift_per_product bg ON bg.product_id = p.id AND bg.rn = 1
        """);

        if (input != null && !input.trim().isEmpty()) {
            queryBuilder.append("""
            WHERE p.name LIKE CONCAT('%', :input, '%')
               OR b.name LIKE CONCAT('%', :input, '%')
               OR c.name LIKE CONCAT('%', :input, '%')
            """);
        }

        queryBuilder.append("""
        GROUP BY p.id, p.name, b.name, c.name, p.image, p.view,
                 p.description, p.instruction, p.ingredient,
                 bg.gift_name, bg.gift_image, bg.gift_stock, bg.start_day, bg.end_day, bg.gift_id
        ORDER BY MAX(pv.create_day) DESC
        """);

        Query query = entityManager.createNativeQuery(queryBuilder.toString());
        if (input != null && !input.trim().isEmpty()) {
            query.setParameter("input", input);
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        long total = results.size();

        return new PageImpl<>(results, pageable, total);
    }

}
