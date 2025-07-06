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
    public Page<Object[]> findByDynamicQuery(Pageable pageable, String name, String brand, String category, Long min_price, Long max_price) {
        StringBuilder queryBuilder = new StringBuilder("""
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
            gp.name AS gift_name,
            pv_gift.image AS gift_image,
            g.stock AS gift_stock,
            g.start_day AS gift_start_day,
            g.end_day AS gift_end_day,
            ROUND(AVG(r.star), 1) AS star,
            g.product_variant_id AS gift_id
        FROM product p
        LEFT JOIN brand b ON p.brand_id = b.id
        LEFT JOIN category c ON p.category_id = c.id
        LEFT JOIN product_variant pv ON pv.product_id = p.id
        LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW()
        LEFT JOIN product_variant_gift pvg ON pvg.product_variant_id = pv.id
        LEFT JOIN gift g ON g.id = pvg.gift_id AND g.start_day <= NOW() AND g.end_day > NOW()
        LEFT JOIN product_variant pv_gift ON g.product_variant_id = pv_gift.id
        LEFT JOIN product gp ON pv_gift.product_id = gp.id
        LEFT JOIN rating r ON r.product_variant_id = pv.id
        LEFT JOIN (
            SELECT pvg.product_variant_id, MAX(pv2.price) AS max_price
            FROM product_variant_gift pvg
            JOIN gift g2 ON g2.id = pvg.gift_id
            JOIN product_variant pv2 ON g2.product_variant_id = pv2.id
            WHERE g2.start_day <= NOW() AND g2.end_day > NOW()
            GROUP BY pvg.product_variant_id
        ) max_gift
        ON max_gift.product_variant_id = pv.id AND pv_gift.price = max_gift.max_price
    """);

        List<String> whereConditions = new ArrayList<>();
        if (name != null) whereConditions.add("p.name LIKE CONCAT('%', :name, '%')");
        if (brand != null) whereConditions.add("p.brand_id = :brand");
        if (category != null) whereConditions.add("p.category_id = :category");

        if (!whereConditions.isEmpty()) {
            queryBuilder.append(" WHERE ").append(String.join(" AND ", whereConditions));
        }

        queryBuilder.append("""
        GROUP BY p.id, p.name, b.name, c.name, p.image, p.view,
                 p.description, p.instruction, p.ingredient,
                 gp.name, pv_gift.image, g.stock, g.start_day, g.end_day, g.product_variant_id
    """);

        List<String> havingConditions = new ArrayList<>();
        if (min_price != null) havingConditions.add("MAX(pv.price) >= :minPrice");
        if (max_price != null) havingConditions.add("""
        COALESCE(
            MIN(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW()
                THEN pv.price * (1 - d.percent / 100.0) END),
            MIN(pv.price)
        ) <= :maxPrice
    """);

        if (!havingConditions.isEmpty()) {
            queryBuilder.append(" HAVING ").append(String.join(" AND ", havingConditions));
        }

        queryBuilder.append(" ORDER BY p.view DESC");

        Query query = entityManager.createNativeQuery(queryBuilder.toString());
        if (name != null) query.setParameter("name", name);
        if (brand != null) query.setParameter("brand", brand);
        if (category != null) query.setParameter("category", category);
        if (min_price != null) query.setParameter("minPrice", min_price);
        if (max_price != null) query.setParameter("maxPrice", max_price);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        // Count query
        Query countQuery = entityManager.createNativeQuery("SELECT COUNT(DISTINCT p.id) FROM product p");
        Long total = (long) results.size(); // Tạm thời đếm thủ công vì count cần GROUP BY phức tạp

        return new PageImpl<>(results, pageable, total);
    }



    @Override
    public Page<Object[]> searchProductsByNameOrBrandOrCategory(Pageable pageable, String input) {
        StringBuilder queryBuilder = new StringBuilder("""
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
            gp.name AS gift_name,
            pv_gift.image AS gift_image,
            g.stock AS gift_stock,
            g.start_day AS gift_start_day,
            g.end_day AS gift_end_day,
            ROUND(AVG(r.star), 1) AS star,
            g.product_variant_id AS gift_id
        FROM product p
        LEFT JOIN brand b ON p.brand_id = b.id
        LEFT JOIN category c ON p.category_id = c.id
        LEFT JOIN product_variant pv ON pv.product_id = p.id
        LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW()
        LEFT JOIN product_variant_gift pvg ON pvg.product_variant_id = pv.id
        LEFT JOIN gift g ON g.id = pvg.gift_id AND g.start_day <= NOW() AND g.end_day > NOW()
        LEFT JOIN product_variant pv_gift ON g.product_variant_id = pv_gift.id
        LEFT JOIN product gp ON pv_gift.product_id = gp.id
        LEFT JOIN rating r ON r.product_variant_id = pv.id
        LEFT JOIN (
            SELECT pvg.product_variant_id, MAX(pv2.price) AS max_price
            FROM product_variant_gift pvg
            JOIN gift g2 ON g2.id = pvg.gift_id
            JOIN product_variant pv2 ON g2.product_variant_id = pv2.id
            WHERE g2.start_day <= NOW() AND g2.end_day > NOW()
            GROUP BY pvg.product_variant_id
        ) max_gift
        ON max_gift.product_variant_id = pv.id AND pv_gift.price = max_gift.max_price
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
                 gp.name, pv_gift.image, g.stock, g.start_day, g.end_day, g.product_variant_id
        ORDER BY p.view DESC
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