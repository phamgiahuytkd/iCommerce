package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Category;
import com.example.iCommerce.entity.Poster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosterRepository extends JpaRepository<Poster, String> {

}
