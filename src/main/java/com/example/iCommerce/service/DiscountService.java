package com.example.iCommerce.service;


import com.example.iCommerce.dto.response.PosterResponse;
import com.example.iCommerce.entity.Discount;
import com.example.iCommerce.entity.Product;
import com.example.iCommerce.entity.ProductVariant;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.PosterMapper;
import com.example.iCommerce.repository.DiscountRepository;
import com.example.iCommerce.repository.PosterRepository;
import com.example.iCommerce.repository.ProductRepository;
import com.example.iCommerce.repository.ProductVariantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountService {
    DiscountRepository discountRepository;
    ProductVariantRepository productVariantRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public void updateDiscount(String productId) {
        ProductVariant productVariant = productVariantRepository.findById(productId).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );

        LocalDateTime now = LocalDateTime.now();
        List<Discount> discounts = discountRepository.findActiveDiscounts(productVariant, now);
        discounts.forEach(discount -> discount.setEnd_day(now));
        discountRepository.saveAll(discounts);
    }


























}
