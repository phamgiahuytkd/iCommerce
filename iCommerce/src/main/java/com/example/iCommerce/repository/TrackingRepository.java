package com.example.iCommerce.repository;

import com.example.iCommerce.entity.InvalidatedToken;
import com.example.iCommerce.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackingRepository extends JpaRepository<Tracking, String> {

}
