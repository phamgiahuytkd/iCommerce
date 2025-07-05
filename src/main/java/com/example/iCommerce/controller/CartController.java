package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.CartIdsRequest;
import com.example.iCommerce.dto.request.CartRequest;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    @PostMapping
    ApiResponse<String> createCart(@RequestBody CartRequest request){
        cartService.createCart(request);
        return ApiResponse.<String>builder()
                .result("Đã thêm sản phẩm vào giỏ hàng!")
                .build();
    }

    @PostMapping("/carts")
    ApiResponse<String> createCarts(@RequestBody List<CartRequest> request){
        cartService.createCarts(request);
        return ApiResponse.<String>builder()
                .result("Đã thêm sản phẩm vào giỏ hàng!")
                .build();
    }

    @PostMapping("/buynow")
    ApiResponse<String> createCartBuyNow(@RequestBody CartRequest request){
        return ApiResponse.<String>builder()
                .result(cartService.createCartBuyNow(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<String> updateCart(@PathVariable String id ,@RequestBody CartRequest request){
        cartService.updateCart(id, request);
        return ApiResponse.<String>builder()
                .result("Đã cập nhật sản phẩm!")
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteCart(@PathVariable String id){
        cartService.deleteCart(id);
        return ApiResponse.<String>builder()
                .result("Đã xóa sản phẩm khỏi giỏ hàng!")
                .build();
    }

    @GetMapping
    ApiResponse<List<CartResponse>> getCarts(){

        return ApiResponse.<List<CartResponse>>builder()
                .result(cartService.getCarts())
                .build();
    }

    @GetMapping("/order/{orderId}")
    ApiResponse<List<CartResponse>> getCartsOrder(@PathVariable String orderId){

        return ApiResponse.<List<CartResponse>>builder()
                .result(cartService.getCartsOrder(orderId))
                .build();
    }



    @GetMapping("/sumcart")
    ApiResponse<Long> sumCartUser(){
        return ApiResponse.<Long>builder()
                .result(cartService.sumCartUser())
                .build();
    }



    @PostMapping("/cartsorder")
    ApiResponse<List<CartResponse>> getCartsOrder(@RequestBody CartIdsRequest request){
        return ApiResponse.<List<CartResponse>>builder()
                .result(cartService.getCartsOrder(request))
                .build();
    }

}
