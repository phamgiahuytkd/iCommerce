package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.Brand;
import com.example.iCommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    List<Address> findAllByUser(User user);
}
