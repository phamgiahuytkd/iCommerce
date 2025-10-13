package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.OrderNowRequest;
import com.example.iCommerce.dto.request.OrderRequest;
import com.example.iCommerce.dto.request.OrderStatusRequest;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.OrderResponse;
import com.example.iCommerce.dto.response.OrderStatusResponse;
import com.example.iCommerce.service.OrderService;
import com.example.iCommerce.service.OrderStatusService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-status")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderStatusController {
    OrderStatusService orderStatusService;


    @PostMapping
    ApiResponse<String> createOrderStatus(@RequestBody OrderStatusRequest request) {
        orderStatusService.createOrderStatus(request);
        return ApiResponse.<String>builder()
                .result("Đã cập nhật trạng thái.")
                .build();

    }

    @GetMapping("/{id}")
    ApiResponse<List<OrderStatusResponse>> getOrderStatus(@PathVariable("id") String id) {
        return ApiResponse.<List<OrderStatusResponse>>builder()
                .result(orderStatusService.getUserOrderStatus(id))
                .build();

    }

    /// deliver ///
    @PostMapping("/deliver")
    ApiResponse<String> createOrderStatusDeliver(@RequestBody OrderStatusRequest request) {
        orderStatusService.createOrderStatusDeliver(request);
        return ApiResponse.<String>builder()
                .result("Đã cập nhật trạng thái.")
                .build();

    }


}
