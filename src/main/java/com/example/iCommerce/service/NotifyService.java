package com.example.iCommerce.service;


import com.example.iCommerce.dto.response.NotifyResponse;
import com.example.iCommerce.entity.Discount;
import com.example.iCommerce.entity.Notify;
import com.example.iCommerce.entity.ProductVariant;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.NotifyMapper;
import com.example.iCommerce.repository.DiscountRepository;
import com.example.iCommerce.repository.NotifyRepository;
import com.example.iCommerce.repository.ProductVariantRepository;
import com.example.iCommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotifyService {
    UserRepository userRepository;
    NotifyRepository notifyRepository;
    NotifyMapper notifyMapper;

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<NotifyResponse> getNotifies() {
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        List<Notify> notifies = notifyRepository.findByUser(user);

        return notifyMapper.toNotifyResponseList(notifies);
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public void deleteNotifyByType_id(String id) {
        notifyRepository.deleteNotifyByType_id(id);
    }


























}
