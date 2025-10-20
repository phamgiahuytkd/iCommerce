package com.example.iCommerce.repository;

import com.example.iCommerce.entity.Notify;
import com.example.iCommerce.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotifyRepository extends JpaRepository<Notify, String> {
    List<Notify> findByUser(User user);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM notify WHERE type_id = :type_id", nativeQuery = true)
    void deleteNotifyByType_id(@Param("type_id") String type_id);

}
