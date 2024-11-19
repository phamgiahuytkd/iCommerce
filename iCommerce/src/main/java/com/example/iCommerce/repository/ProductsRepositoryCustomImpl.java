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
        public List<Products> findByDynamicQuery(String name, String brand, String colour, Long minPrice, Long maxPrice) {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM products p WHERE 1=1");

            // Điều kiện cho 'name'
            if (name != null) {
                queryBuilder.append(" AND p.name LIKE CONCAT('%', :name, '%') COLLATE utf8mb4_unicode_ci");
            }

            // Điều kiện cho 'brand'
            if (brand != null) {
                queryBuilder.append(" AND p.brand LIKE CONCAT('%', :brand, '%') COLLATE utf8mb4_unicode_ci");
            }

            // Điều kiện cho 'colour'
            if (colour != null) {
                queryBuilder.append(" AND p.colour LIKE CONCAT('%', :colour, '%') COLLATE utf8mb4_unicode_ci");
            }

            // Điều kiện cho 'minPrice'
            if (minPrice != null) {
                queryBuilder.append(" AND p.price >= :minPrice");
            }

            // Điều kiện cho 'maxPrice'
            if (maxPrice != null) {
                queryBuilder.append(" AND p.price <= :maxPrice");
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

            if (minPrice != null) {
                query.setParameter("minPrice", minPrice);
            }

            if (maxPrice != null) {
                query.setParameter("maxPrice", maxPrice);
            }

            return query.getResultList();

        }



    }

