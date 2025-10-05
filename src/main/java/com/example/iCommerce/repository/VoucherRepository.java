package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {
    @Query("SELECT v FROM Voucher v WHERE v.start_day <= :now AND v.end_day >= :now")
    List<Voucher> findValidVouchers(@Param("now") LocalDateTime now);

}
