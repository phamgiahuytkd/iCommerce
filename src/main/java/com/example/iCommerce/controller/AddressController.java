package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.service.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {

    AddressService addressService;

    @PostMapping
    ApiResponse<String> createAddress(@RequestBody AddressRequest request){
        addressService.createAddress(request);
        return ApiResponse.<String>builder()
                .result("Đã thêm địa chỉ.")
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteCart(@PathVariable String id){
        addressService.deleteAddress(id);
        return ApiResponse.<String>builder()
                .result("Đã xóa địa chỉ.")
                .build();
    }


    @GetMapping
    ApiResponse<List<AddressResponse>> getAddressesByUser(){
        return ApiResponse.<List<AddressResponse>>builder()
                .result(addressService.getAddressesByUser())
                .build();
    }


}
