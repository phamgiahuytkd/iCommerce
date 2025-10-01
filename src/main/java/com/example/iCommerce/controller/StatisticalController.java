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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/statistical")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticalController {

    StatisticalService statisticalService;

    @GetMapping("/overview/{type}/{date}")
    ApiResponse<OverviewResponse> getOverview(@PathVariable String type, @PathVariable LocalDate date){
        return ApiResponse.<OverviewResponse>builder()
                .result(statisticalService.getOverview(type, date))
                .build();
    }


    @GetMapping("/revenue-by-category/{type}/{date}")
    ApiResponse<List<RevenueByCategoryResponse>> getRevenueByCategory(@PathVariable String type, @PathVariable LocalDate date){
        return ApiResponse.<List<RevenueByCategoryResponse>>builder()
                .result(statisticalService.getRevenueByCategory(type, date))
                .build();
    }

    @GetMapping("/revenue-by-brand/{type}/{date}")
    ApiResponse<List<RevenueByCategoryResponse>> getRevenueByBrand(@PathVariable String type, @PathVariable LocalDate date){
        return ApiResponse.<List<RevenueByCategoryResponse>>builder()
                .result(statisticalService.getRevenueByBrand(type, date))
                .build();
    }

    @GetMapping("/revenue-by-date/{type}/{date}")
    ApiResponse<List<Object[]>> getRevenueByDate(@PathVariable String type, @PathVariable LocalDate date){
        return ApiResponse.<List<Object[]>>builder()
                .result(statisticalService.getRevenueByDate(type, date))
                .build();
    }

    @GetMapping("/top-selling-product/{type}/{date}")
    ApiResponse<List<Object[]>> getTopSellingProducts(@PathVariable String type, @PathVariable LocalDate date){
        return ApiResponse.<List<Object[]>>builder()
                .result(statisticalService.getTopSellingProducts(type, date))
                .build();
    }


    @GetMapping("/lowest-stock")
    ApiResponse<List<Object[]>> getLowestStockProductVariants(){
        return ApiResponse.<List<Object[]>>builder()
                .result(statisticalService.getLowestStockProductVariants())
                .build();
    }

    @GetMapping("/top-gift-selected/{type}/{date}")
    ApiResponse<List<Object[]>> getTopGiftSelected(@PathVariable String type, @PathVariable LocalDate date){
        return ApiResponse.<List<Object[]>>builder()
                .result(statisticalService.getTopGiftSelected(type, date))
                .build();
    }


}
