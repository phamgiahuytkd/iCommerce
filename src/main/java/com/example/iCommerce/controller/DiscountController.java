package com.example.iCommerce.controller;

import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.PosterResponse;
import com.example.iCommerce.service.DiscountService;
import com.example.iCommerce.service.PosterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/discount")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountController {

    DiscountService discountService;

    @PutMapping("/{id}")
    ApiResponse<String> updateDiscount(@PathVariable("id") String id) {
        discountService.updateDiscount(id);
        return ApiResponse.<String>builder()
                .result("Đã dừng khuyến mãi")
                .build();
    }
}
