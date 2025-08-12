package com.example.iCommerce.repository;

import com.example.iCommerce.entity.*;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, DiscountId> {
    @Query("SELECT d FROM Discount d WHERE d.productVariant = :variant AND d.end_day > :now")
    List<Discount> findActiveDiscounts(@Param("variant") ProductVariant variant, @Param("now") LocalDateTime now);
}
