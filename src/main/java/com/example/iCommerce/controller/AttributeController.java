package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.request.AttributeRequest;
import com.example.iCommerce.dto.request.AttributeValueRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.AttributeResponse;
import com.example.iCommerce.service.AddressService;
import com.example.iCommerce.service.AttributeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/attribute")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttributeController {
    AttributeService attributeService;

    @PostMapping
    ApiResponse<String> createAttribute(@RequestBody AttributeRequest request){
        attributeService.createAttribute(request);
        return ApiResponse.<String>builder()
                .result("Đã thêm thuộc tính.")
                .build();
    }

    @PostMapping("/value")
    ApiResponse<String> createAttributeValue(@RequestBody AttributeValueRequest request){
        attributeService.createAttributeValue(request);
        return ApiResponse.<String>builder()
                .result("Đã thêm giá trị thuộc tính.")
                .build();
    }



    @GetMapping
    ApiResponse<List<AttributeResponse>> getAttributes(){
        return ApiResponse.<List<AttributeResponse>>builder()
                .result(attributeService.getAttributes())
                .build();
    }

}
