package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.request.VoucherRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.VoucherResponse;
import com.example.iCommerce.service.AddressService;
import com.example.iCommerce.service.VoucherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/voucher")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherController {

    VoucherService voucherService;

    @PostMapping
    ApiResponse<String> createVoucher(@RequestBody VoucherRequest request){
        voucherService.createVoucher(request);
        return ApiResponse.<String>builder()
                .result("Đã thêm mã giảm giá.")
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<String> updateVoucher(@PathVariable("id") String id, @RequestBody VoucherRequest request){
        voucherService.updateVoucher(id, request);
        return ApiResponse.<String>builder()
                .result("Đã cập nhật mã giảm giá.")
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteVoucher(@PathVariable String id){
        voucherService.deleteVoucher(id);
        return ApiResponse.<String>builder()
                .result("Đã xóa mã khuyến mãi.")
                .build();
    }


    @GetMapping
    ApiResponse<List<VoucherResponse>> getVouchers(){
        return ApiResponse.<List<VoucherResponse>>builder()
                .result(voucherService.getVouchers())
                .build();
    }

    @GetMapping("/user")
    ApiResponse<List<VoucherResponse>> getVouchersByUser(){
        return ApiResponse.<List<VoucherResponse>>builder()
                .result(voucherService.getVouchersByUser())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<VoucherResponse> getVoucher(@PathVariable("id") String id){
        return ApiResponse.<VoucherResponse>builder()
                .result(voucherService.getVoucher(id))
                .build();
    }

    @GetMapping("/{id}/order")
    ApiResponse<VoucherResponse> getVoucherByOrder(@PathVariable("id") String id){
        return ApiResponse.<VoucherResponse>builder()
                .result(voucherService.getVoucherByOrder(id))
                .build();
    }


}
