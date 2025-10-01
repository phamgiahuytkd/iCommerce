package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {
}
