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
        // Query chính
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT " +
                        "p.id, " +
                        "p.name, " +
                        "COALESCE(" +
                        "   MIN(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN pv.price * (1 - d.percent / 100.0) END)," +
                        "   MIN(pv.price)" +
                        ") AS price, " +
                        "b.name, " +
                        "c.name, " +
                        "p.image, " +
                        "p.view, " +
                        "SUM(pv.stock) AS stock, " +
                        "MAX(pv.create_day) AS create_day, " +
                        "MAX(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN d.percent END) AS percent, " +
                        "gp.name AS gift_name, " +
                        "pv_gift.image AS gift_image, " +
                        "g.stock AS gift_stock, " +
                        "g.start_day AS gift_start_day, " +
                        "g.end_day AS gift_end_day, " +
                        "ROUND(AVG(r.star), 1) AS star, " +
                        "g.product_variant_id AS gift_id " +
                        "FROM product p " +
                        "LEFT JOIN product_variant pv ON pv.product_id = p.id " +
                        "LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW() " +
                        "LEFT JOIN gift g ON p.gift_id = g.id AND g.end_day > NOW() AND g.start_day <= NOW() " +
                        "LEFT JOIN product_variant pv_gift ON g.product_variant_id = pv_gift.id " +
                        "LEFT JOIN product gp ON pv_gift.product_id = gp.id " +
                        "LEFT JOIN rating r ON r.product_variant_id = pv.id " +
                        "LEFT JOIN brand b ON p.brand_id = b.id " +
                        "LEFT JOIN category c ON p.category_id = c.id "
        );


        // Thêm điều kiện động cho WHERE
        List<String> whereConditions = new ArrayList<>();
        if (name != null) {
            whereConditions.add("p.name LIKE CONCAT('%', :name, '%')");
        }
        if (brand != null) {
            whereConditions.add("p.brand_id = :brand");
        }
        if (category != null) {
            whereConditions.add("p.category_id = :category");
        }

        if (!whereConditions.isEmpty()) {
            queryBuilder.append(" WHERE ").append(String.join(" AND ", whereConditions));
        }

        // Thêm GROUP BY
        queryBuilder.append(
                " GROUP BY " +
                        "p.id, p.name, p.image, p.view, " +
                        "b.name, c.name, " +  // thêm brand, category
                        "gp.name, pv_gift.image, " +
                        "g.stock, g.start_day, g.end_day, g.product_variant_id"
        );

        // Thêm điều kiện HAVING cho min_price và max_price
        List<String> havingConditions = new ArrayList<>();
        if (min_price != null) {
            havingConditions.add("MAX(pv.price) >= :minPrice");
        }
        if (max_price != null) {
            havingConditions.add(
                    "COALESCE(" +
                            "MIN(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() " +
                            "THEN pv.price * (1 - d.percent / 100.0) END)," +
                            "MIN(pv.price)" +
                            ") <= :maxPrice"
            );
        }

        if (!havingConditions.isEmpty()) {
            queryBuilder.append(" HAVING ").append(String.join(" AND ", havingConditions));
        }

        // Thêm ORDER BY
        queryBuilder.append(" ORDER BY p.view DESC");

        // Query đếm tổng số bản ghi
        StringBuilder countQueryBuilder = new StringBuilder(
                "SELECT COUNT(DISTINCT p.id) " +
                        "FROM product p " +
                        "LEFT JOIN product_variant pv ON pv.product_id = p.id " +
                        "LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW() " +
                        "LEFT JOIN gift g ON p.gift_id = g.id AND g.end_day > NOW() AND g.start_day <= NOW() " +
                        "LEFT JOIN product_variant pv_gift ON g.product_variant_id = pv_gift.id " +
                        "LEFT JOIN product gp ON pv_gift.product_id = gp.id " +
                        "LEFT JOIN rating r ON r.product_variant_id = pv.id " +
                        "LEFT JOIN brand b ON p.brand_id = b.id " +
                        "LEFT JOIN category c ON p.category_id = c.id "
        );

        if (!whereConditions.isEmpty()) {
            countQueryBuilder.append(" WHERE ").append(String.join(" AND ", whereConditions));
        }

        if (!havingConditions.isEmpty()) {
            countQueryBuilder.append(" GROUP BY p.id HAVING ").append(String.join(" AND ", havingConditions));
        }

        // Tạo query chính
        Query query = entityManager.createNativeQuery(queryBuilder.toString());
        Query countQuery = entityManager.createNativeQuery(countQueryBuilder.toString());

        // Gán tham số cho query chính và count query
        if (name != null) {
            query.setParameter("name", name);
            countQuery.setParameter("name", name);
        }
        if (brand != null) {
            query.setParameter("brand", brand);
            countQuery.setParameter("brand", brand);
        }
        if (category != null) {
            query.setParameter("category", category);
            countQuery.setParameter("category", category);
        }
        if (min_price != null) {
            query.setParameter("minPrice", min_price);
            countQuery.setParameter("minPrice", min_price);
        }
        if (max_price != null) {
            query.setParameter("maxPrice", max_price);
            countQuery.setParameter("maxPrice", max_price);
        }

        // Áp dụng phân trang
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Lấy kết quả
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        // Lấy tổng số bản ghi
        Long totalRecords = ((Number) countQuery.getSingleResult()).longValue();
        long total = totalRecords;

        // Trả về Page<Object[]>
        return new PageImpl<>(results, pageable, total);
    }



    @Override
    public Page<Object[]> searchProductsByNameOrBrandOrCategory(Pageable pageable, String input) {
        // Query chính
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT " +
                        "p.id, " +
                        "p.name, " +
                        "COALESCE(" +
                        "   MIN(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN pv.price * (1 - d.percent / 100.0) END)," +
                        "   MIN(pv.price)" +
                        ") AS price, " +
                        "b.name, " +
                        "c.name, " +
                        "p.image, " +
                        "p.view, " +
                        "SUM(pv.stock) AS stock, " +
                        "MAX(pv.create_day) AS create_day, " +
                        "MAX(CASE WHEN d.end_day > NOW() AND d.start_day <= NOW() THEN d.percent END) AS percent, " +
                        "gp.name AS gift_name, " +
                        "pv_gift.image AS gift_image, " +
                        "g.stock AS gift_stock, " +
                        "g.start_day AS gift_start_day, " +
                        "g.end_day AS gift_end_day, " +
                        "ROUND(AVG(r.star), 1) AS star, " +
                        "g.product_variant_id AS gift_id " +
                        "FROM product p " +
                        "LEFT JOIN product_variant pv ON pv.product_id = p.id " +
                        "LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW() " +
                        "LEFT JOIN gift g ON p.gift_id = g.id AND g.end_day > NOW() AND g.start_day <= NOW() " +
                        "LEFT JOIN product_variant pv_gift ON g.product_variant_id = pv_gift.id " +
                        "LEFT JOIN product gp ON pv_gift.product_id = gp.id " +
                        "LEFT JOIN rating r ON r.product_variant_id = pv.id " +
                        "LEFT JOIN brand b ON p.brand_id = b.id " +
                        "LEFT JOIN category c ON p.category_id = c.id "
        );

        // Thêm điều kiện động cho WHERE với OR
        List<String> whereConditions = new ArrayList<>();
        if (input != null && !input.trim().isEmpty()) {
            whereConditions.add("p.name LIKE CONCAT('%', :input, '%')");
            whereConditions.add("p.brand_id LIKE CONCAT('%', :input, '%')");
            whereConditions.add("p.category_id LIKE CONCAT('%', :input, '%')");
        }

        if (!whereConditions.isEmpty()) {
            queryBuilder.append(" WHERE ").append(String.join(" OR ", whereConditions));
        }

        // Thêm GROUP BY
        queryBuilder.append(
                " GROUP BY p.id, p.name, b.name, c.name, p.image, p.view, " +
                        "gp.name, pv_gift.image, g.stock, g.start_day, g.end_day, g.product_variant_id"
        );

        // Thêm ORDER BY
        queryBuilder.append(" ORDER BY p.view DESC");

        // Query đếm tổng số bản ghi
        StringBuilder countQueryBuilder = new StringBuilder(
                "SELECT COUNT(DISTINCT p.id) " +
                        "FROM product p " +
                        "LEFT JOIN product_variant pv ON pv.product_id = p.id " +
                        "LEFT JOIN discount d ON d.product_variant_id = pv.id AND d.end_day > NOW() AND d.start_day <= NOW() " +
                        "LEFT JOIN gift g ON p.gift_id = g.id AND g.end_day > NOW() AND g.start_day <= NOW() " +
                        "LEFT JOIN product_variant pv_gift ON g.product_variant_id = pv_gift.id " +
                        "LEFT JOIN product gp ON pv_gift.product_id = gp.id " +
                        "LEFT JOIN rating r ON r.product_variant_id = pv.id " +
                        "LEFT JOIN brand b ON p.brand_id = b.id " +
                        "LEFT JOIN category c ON p.category_id = c.id "
        );

        if (!whereConditions.isEmpty()) {
            countQueryBuilder.append(" WHERE ").append(String.join(" OR ", whereConditions));
        }

        // Tạo query chính
        Query query = entityManager.createNativeQuery(queryBuilder.toString());
        Query countQuery = entityManager.createNativeQuery(countQueryBuilder.toString());

        // Gán tham số cho query chính và count query
        if (input != null && !input.trim().isEmpty()) {
            query.setParameter("input", input);
            countQuery.setParameter("input", input);
        }

        // Áp dụng phân trang
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Lấy kết quả
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        // Lấy tổng số bản ghi
        Long totalRecords = ((Number) countQuery.getSingleResult()).longValue();
        long total = totalRecords;

        // Trả về Page<Object[]>
        return new PageImpl<>(results, pageable, total);
    }
}