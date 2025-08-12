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

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticalService {

    OrderRepository orderRepository;
    ProductVariantRepository productVariantRepository;
    ProductVariantMapper productVariantMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public OverviewResponse getOverview(String type) {
        List<Object[]> raw = orderRepository.getOverview(type);
        Object[] data = raw.get(0);
        return OverviewResponse.builder()
                .total_orders(((Number) data[0]).longValue())
                .total_revenue(((Number) data[1]).longValue())
                .average_order(((Number) data[2]).doubleValue())
                .total_sold_products(((Number) data[3]).longValue())
                .build();
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<RevenueByCategoryResponse> getRevenueByCategory(String type) {
        List<Object[]> raw = orderRepository.getRevenueByCategory(type);

        return raw.stream()
                .map(obj -> RevenueByCategoryResponse.builder()
                        .id(obj[0].toString()) // ép sang String
                        .name((String) obj[1])
                        .revenue(
                                obj[2] != null ? ((Number) obj[2]).longValue() : 0L
                        )
                        .build())
                .toList();
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<Object[]> getRevenueByDate(String type) {
        List<Object[]> raw = orderRepository.getRevenueByDate(type);
        return raw;
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<Object[]> getTopSellingProducts(String type) {
        List<Object[]> raw = orderRepository.getTopSellingProducts(type);
        return raw;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Object[]> getLowestStockProductVariants() {
        List<Object[]> raw = productVariantRepository.findLowestStockProductVariants();
        return raw;
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<Object[]> getTopGiftSelected(String type) {
        List<Object[]> raw = orderRepository.findTopGiftSelected(type);
        return raw;
    }

















}
