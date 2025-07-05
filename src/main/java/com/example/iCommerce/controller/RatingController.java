package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.ProductRequest;
import com.example.iCommerce.dto.request.RatingRequest;
import com.example.iCommerce.dto.response.*;
import com.example.iCommerce.service.BrandService;
import com.example.iCommerce.service.RatingService;
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
@RequestMapping("/rating")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingController {

    RatingService ratingService;

    @GetMapping("/{id}")
    public ApiResponse<List<StarCountResponse>> getStarCounts(@PathVariable("id") String id) {
        return ApiResponse.<List<StarCountResponse>>builder()
                .result(ratingService.getStarCountsForProduct(id))
                .build();
    }

    @GetMapping("/{id}/ratings")
    public ApiResponse<List<RatingResponse>> getProductRatings(@PathVariable("id") String id) {
        return ApiResponse.<List<RatingResponse>>builder()
                .result(ratingService.getAllRatingsForProduct(id))
                .build();
    }


    @GetMapping("/product-to-rate")
    public ApiResponse<List<ProductToRateResponse>> getProductsToRate() {
        return ApiResponse.<List<ProductToRateResponse>>builder()
                .result(ratingService.getProductsToRate())
                .build();
    }


    @PostMapping
    public ApiResponse<String> createRating(
            @ModelAttribute RatingRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {
        ratingService.createRating(request, images); // truyền list ảnh vào service
        return ApiResponse.<String>builder()
                .result("Đã đăng đánh giá.")
                .build();
    }



    @GetMapping("/product-rated")
    public ApiResponse<List<UserRatingResponse>> getAllRatingsOfUser() {
        return ApiResponse.<List<UserRatingResponse>>builder()
                .result(ratingService.getAllRatingsOfUser())
                .build();
    }





}
