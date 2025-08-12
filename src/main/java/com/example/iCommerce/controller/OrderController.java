package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.OrderNowRequest;
import com.example.iCommerce.dto.request.OrderRequest;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.OrderResponse;
import com.example.iCommerce.service.OrderService;
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
    ApiResponse<String> createOrder(@RequestBody OrderRequest request){
        return ApiResponse.<String>builder()
                .result(orderService.createOrder(request))
                .build();
    }

    @PostMapping("/buynow")
    ApiResponse<String> createOrderBuyNow(@RequestBody OrderNowRequest request){
        return ApiResponse.<String>builder()
                .result(orderService.createOrderBuyNow(request))
                .build();
    }



    @PutMapping("/{id}")
    ApiResponse<OrderResponse> updateOrder(@PathVariable("id") String id, @RequestBody OrderRequest request){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrder(id, request))
                .build();

    }



    @GetMapping("/{id}")
    ApiResponse<OrderResponse> getOrder(@PathVariable("id") String id){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrder(id))
                .build();
    }


    @GetMapping("/myorder")
    ApiResponse<List<OrderResponse>> getUserOrders(){
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getUserOrders())
                .build();
    }





////////////////////////////////////
    @GetMapping("/status/{status}")
    ApiResponse<List<OrderResponse>> getUserOrdersPerStatus(@PathVariable("status") String status){
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getUserOrdersPerStatus(status))
                .build();
    }


    /// admin ///
    @GetMapping("/allorder/{status}")
    ApiResponse<List<OrderResponse>> getOrders(@PathVariable("status") String status){

        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getOrders(status))
                .build();
    }
    






}

