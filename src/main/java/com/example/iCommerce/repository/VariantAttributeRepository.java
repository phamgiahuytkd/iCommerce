package com.example.iCommerce.repository;

import com.example.iCommerce.entity.AttributeValue;
import com.example.iCommerce.entity.VariantAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantAttributeRepository extends JpaRepository<VariantAttribute, String> {

}
