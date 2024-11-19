package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.CartCreationRequest;
import com.example.iCommerce.dto.request.ProductsCreationRequest;
import com.example.iCommerce.dto.request.ProductsUpdateRequest;
import com.example.iCommerce.dto.request.SearchProductsRequest;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.dto.response.ProductsResponse;
import com.example.iCommerce.entity.Products;
import com.example.iCommerce.repository.CartRepository;
import com.example.iCommerce.service.ProductsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductsService productsService;


    @PostMapping
    ApiResponse<ProductsResponse> createProducts(@RequestBody ProductsCreationRequest request){
        return ApiResponse.<ProductsResponse>builder()
                .result(productsService.createProducts(request))
                .build();
    }

    @PutMapping("/view/{id}")
    ApiResponse<ProductsResponse> updateProducts(@PathVariable("id") String id, @RequestBody ProductsUpdateRequest request){
        return ApiResponse.<ProductsResponse>builder()
                .result( productsService.updateProducts(id, request))
                .build();

    }



    @DeleteMapping("/view/{id}")
    ApiResponse<String> deleteProducts(@PathVariable String id){
        productsService.deleteProducts(id);
        return ApiResponse.<String>builder()
                .result("SUCCEED")
                .build();
    }


    @GetMapping("/view/{id}")
    ApiResponse<ProductsResponse> getProduct(@PathVariable("id") String id){

        return ApiResponse.<ProductsResponse>builder()
                .result(productsService.getProduct(id))
                .build();
    }




    @GetMapping
    ApiResponse<List<ProductsResponse>> getProducts(){

        return ApiResponse.<List<ProductsResponse>>builder()
                .result(productsService.getProducts())
                .build();
    }

    @PostMapping("/search")
    ApiResponse<List<ProductsResponse>> getSearchProducts(@RequestBody SearchProductsRequest request){
        return ApiResponse.<List<ProductsResponse>>builder()
                .result(productsService.getSearchProducts(request))
                .build();
    }






    //CART


    @PostMapping("/cart")
    ApiResponse<CartResponse> createCart(@RequestBody CartCreationRequest request){
        return ApiResponse.<CartResponse>builder()
                .result(productsService.createCart(request))
                .build();
    }


    @DeleteMapping("/cart/{id}")
    ApiResponse<String> deleteCart(@PathVariable String id){
        productsService.deleteCart(id);
        return ApiResponse.<String>builder()
                .result("Succeed")
                .build();
    }

    @GetMapping("/cart")
    ApiResponse<List<CartResponse>> getCarts(){

        return ApiResponse.<List<CartResponse>>builder()
                .result(productsService.getCarts())
                .build();
    }



}
