package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.ProductsCreationRequest;
import com.example.iCommerce.dto.request.ProductsUpdateRequest;
import com.example.iCommerce.dto.request.UserCreationRequest;
import com.example.iCommerce.dto.request.UserUpdateRequest;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.ProductsResponse;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.service.ProductsService;
import com.example.iCommerce.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{id}")
    ApiResponse<ProductsResponse> updateProducts(@PathVariable("id") String id, @RequestBody ProductsUpdateRequest request){
        return ApiResponse.<ProductsResponse>builder()
                .result( productsService.updateProducts(id, request))
                .build();

    }



    @DeleteMapping("/{id}")
    ApiResponse<String> deleteProducts(@PathVariable String id){
        productsService.deleteProducts(id);
        return ApiResponse.<String>builder()
                .result("SUCCEED")
                .build();
    }


    @GetMapping("/{id}")
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



}
