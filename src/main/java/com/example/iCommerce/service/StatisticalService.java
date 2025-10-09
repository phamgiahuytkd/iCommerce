package com.example.iCommerce.service;


import com.example.iCommerce.dto.response.OverviewResponse;
import com.example.iCommerce.dto.response.ProductVariantResponse;
import com.example.iCommerce.dto.response.RevenueByCategoryResponse;
import com.example.iCommerce.mapper.ProductVariantMapper;
import com.example.iCommerce.repository.OrderRepository;
import com.example.iCommerce.repository.ProductVariantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticalService {

    OrderRepository orderRepository;
    ProductVariantRepository productVariantRepository;
    ProductVariantMapper productVariantMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public OverviewResponse getOverview(String type, LocalDate date) {
        List<Object[]> raw = orderRepository.getOverview(type, date);
        Object[] data = raw.get(0);
        return OverviewResponse.builder()
                .total_orders(((Number) data[0]).longValue())
                .total_revenue(data[1] != null ? ((Number) data[1]).longValue() : 0L)
                .average_order(data[2] != null ? ((Number) data[2]).doubleValue() : 0.0)
                .total_sold_products(data[3] != null ? ((Number) data[3]).longValue() : 0L)
                .build();
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<RevenueByCategoryResponse> getRevenueByCategory(String type, LocalDate date) {
        List<Object[]> raw = orderRepository.getRevenueByCategory(type, date);

        return raw.stream()
                .map(obj -> RevenueByCategoryResponse.builder()
                        .id(obj[0] != null ? obj[0].toString() : "") // tránh NullPointerException
                        .name(obj[1] != null ? obj[1].toString() : "Không xác định") // fallback name
                        .revenue(obj[2] != null ? ((Number) obj[2]).longValue() : 0L)
                        .build())
                .toList();
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<RevenueByCategoryResponse> getRevenueByBrand(String type, LocalDate date) {
        List<Object[]> raw = orderRepository.getRevenueByBrand(type, date);

        return raw.stream()
                .map(obj -> RevenueByCategoryResponse.builder()
                        .id(obj[0] != null ? obj[0].toString() : "") // tránh lỗi null
                        .name(obj[1] != null ? obj[1].toString() : "Không xác định") // fallback name
                        .revenue(obj[2] != null ? ((Number) obj[2]).longValue() : 0L)
                        .build())
                .toList();
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<Object[]> getRevenueByDate(String type, LocalDate date) {
        List<Object[]> raw = orderRepository.getRevenueByDate(type, date);
        return raw;
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<Object[]> getTopSellingProducts(String type, LocalDate date) {
        List<Object[]> raw = orderRepository.getTopSellingProducts(type, date);
        return raw;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Object[]> getLowestStockProductVariants() {
        List<Object[]> raw = productVariantRepository.findLowestStockProductVariants();
        return raw;
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<Object[]> getTopGiftSelected(String type, LocalDate date) {
        List<Object[]> raw = orderRepository.findTopGiftSelected(type, date);
        return raw;
    }

















}
