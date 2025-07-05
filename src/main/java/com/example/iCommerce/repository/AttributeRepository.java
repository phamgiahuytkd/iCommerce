package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Attribute;
import com.example.iCommerce.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, String> {
    Optional<Attribute> findByName(String name);
    boolean existsByName(String name);

    @EntityGraph(attributePaths = "attributeValues")
    List<Attribute> findAll();

}
