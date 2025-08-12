package com.example.iCommerce.controller;

import com.example.iCommerce.dto.response.*;
import com.example.iCommerce.service.BrandService;
import com.example.iCommerce.service.StatisticalService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/statistical")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticalController {

    StatisticalService statisticalService;

    @GetMapping("/overview/{type}")
    ApiResponse<OverviewResponse> getOverview(@PathVariable String type){
        return ApiResponse.<OverviewResponse>builder()
                .result(statisticalService.getOverview(type))
                .build();
    }


    @GetMapping("/revenue-by-category/{type}")
    ApiResponse<List<RevenueByCategoryResponse>> getRevenueByCategory(@PathVariable String type){
        return ApiResponse.<List<RevenueByCategoryResponse>>builder()
                .result(statisticalService.getRevenueByCategory(type))
                .build();
    }

    @GetMapping("/revenue-by-date/{type}")
    ApiResponse<List<Object[]>> getRevenueByDate(@PathVariable String type){
        return ApiResponse.<List<Object[]>>builder()
                .result(statisticalService.getRevenueByDate(type))
                .build();
    }

    @GetMapping("/top-selling-product/{type}")
    ApiResponse<List<Object[]>> getTopSellingProducts(@PathVariable String type){
        return ApiResponse.<List<Object[]>>builder()
                .result(statisticalService.getTopSellingProducts(type))
                .build();
    }


    @GetMapping("/lowest-stock")
    ApiResponse<List<Object[]>> getLowestStockProductVariants(){
        return ApiResponse.<List<Object[]>>builder()
                .result(statisticalService.getLowestStockProductVariants())
                .build();
    }

    @GetMapping("/top-gift-selected/{type}")
    ApiResponse<List<Object[]>> getTopGiftSelected(@PathVariable String type){
        return ApiResponse.<List<Object[]>>builder()
                .result(statisticalService.getTopGiftSelected(type))
                .build();
    }


}
