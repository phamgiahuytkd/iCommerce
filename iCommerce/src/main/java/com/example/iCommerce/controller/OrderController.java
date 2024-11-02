package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.*;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.dto.response.OrdersResponse;
import com.example.iCommerce.dto.response.ProductsResponse;
import com.example.iCommerce.repository.CartRepository;
import com.example.iCommerce.service.OrderService;
import com.example.iCommerce.service.ProductsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;


    @PostMapping
    ApiResponse<OrdersResponse> createOrders(@RequestBody OrdersCreationRequest request){
        return ApiResponse.<OrdersResponse>builder()
                .result(orderService.createOrders(request))
                .build();
    }



    @PutMapping("/{id}")
    ApiResponse<String> updateOrder(@PathVariable("id") String id, @RequestBody OrdersUpdateRequest request){
        orderService.updateOrder(id, request);
        return ApiResponse.<String>builder()
                .result("SUCCEED")
                .build();

    }


//
//    @DeleteMapping("/{id}")
//    ApiResponse<String> deleteProducts(@PathVariable String id){
//        productsService.deleteProducts(id);
//        return ApiResponse.<String>builder()
//                .result("SUCCEED")
//                .build();
//    }
//
//
//    @GetMapping("/{id}")
//    ApiResponse<ProductsResponse> getProduct(@PathVariable("id") String id){
//
//        return ApiResponse.<ProductsResponse>builder()
//                .result(productsService.getProduct(id))
//                .build();
//    }
//
//
//
//
//    @GetMapping
//    ApiResponse<List<ProductsResponse>> getProducts(){
//
//        return ApiResponse.<List<ProductsResponse>>builder()
//                .result(productsService.getProducts())
//                .build();
//    }
//
//
//
//    @PostMapping("/cart")
//    ApiResponse<CartResponse> createCart(@RequestBody CartCreationRequest request){
//        return ApiResponse.<CartResponse>builder()
//                .result(productsService.createCart(request))
//                .build();
//    }
//
//
//    @DeleteMapping("/cart/{id}")
//    ApiResponse<String> deleteCart(@PathVariable String id){
//        productsService.deleteCart(id);
//        return ApiResponse.<String>builder()
//                .result("Succeed")
//                .build();
//    }
//
//    @GetMapping("/cart")
//    ApiResponse<List<CartResponse>> getCarts(){
//
//        return ApiResponse.<List<CartResponse>>builder()
//                .result(productsService.getCarts())
//                .build();
//    }



}
