package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Attribute;
import com.example.iCommerce.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue, String> {
    boolean existsByName(String name);

    boolean existsByNameIn(List<String> names);
}
