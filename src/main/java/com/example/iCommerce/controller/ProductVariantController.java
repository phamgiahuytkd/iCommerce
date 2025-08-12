package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.LoveProductRequest;
import com.example.iCommerce.dto.request.ProductRequest;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.PriceRangeResponse;
import com.example.iCommerce.dto.response.ProductResponse;
import com.example.iCommerce.dto.response.ProductVariantResponse;
import com.example.iCommerce.service.ProductService;
import com.example.iCommerce.service.ProductVariantService;
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
@RequestMapping("/product-variant")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantController {
    ProductVariantService productVariantService;

    @GetMapping
    ApiResponse<List<ProductVariantResponse>> getProductVariants(){

        return ApiResponse.<List<ProductVariantResponse>>builder()
                .result(productVariantService.getProductVariants())
                .build();
    }


    @GetMapping("/{id}")
    ApiResponse<List<ProductVariantResponse>> getProductVariantsPerProduct(@PathVariable("id") String id){

        return ApiResponse.<List<ProductVariantResponse>>builder()
                .result(productVariantService.getProductVariantsPerProduct(id))
                .build();
    }

    @GetMapping("/{id}/admin")
    ApiResponse<List<ProductVariantResponse>> getProductVariantsPerProductAdmin(@PathVariable("id") String id){

        return ApiResponse.<List<ProductVariantResponse>>builder()
                .result(productVariantService.getProductVariantsPerProductAdmin(id))
                .build();
    }

    @GetMapping("/price-range")
    ApiResponse<PriceRangeResponse> getPriceRange(){

        return ApiResponse.<PriceRangeResponse>builder()
                .result(productVariantService.getPriceRange())
                .build();
    }

    @PutMapping("/{id}/stop")
    ApiResponse<String> updateStopProductVariant(@PathVariable("id") String id){
        productVariantService.updateStopProductVariant(id);
        return ApiResponse.<String>builder()
                .result("Đã dừng bán sản phẩm")
                .build();
    }

    @PutMapping("/{id}/continue")
    ApiResponse<String> updateContinueProductVariant(@PathVariable("id") String id){
        productVariantService.updateContinueProductVariant(id);
        return ApiResponse.<String>builder()
                .result("Đã mở bán sản phẩm")
                .build();
    }


}
