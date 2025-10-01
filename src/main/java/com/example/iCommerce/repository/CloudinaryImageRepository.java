package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.CloudinaryImage;
import com.example.iCommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CloudinaryImageRepository extends JpaRepository<CloudinaryImage, String> {
}
