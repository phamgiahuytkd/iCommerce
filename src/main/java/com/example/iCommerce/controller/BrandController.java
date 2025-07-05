package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.BrandRequest;
import com.example.iCommerce.dto.request.CartRequest;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.dto.response.CategoryResponse;
import com.example.iCommerce.service.BrandService;
import com.example.iCommerce.service.CategoryService;
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
@RequestMapping("/brand")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandController {

    BrandService brandService;

    @GetMapping
    ApiResponse<List<BrandResponse>> getBrands(){
        return ApiResponse.<List<BrandResponse>>builder()
                .result(brandService.getBrands())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<BrandResponse> getBrand(@PathVariable("id") String id){
        return ApiResponse.<BrandResponse>builder()
                .result(brandService.getBrand(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<String> updateBrand(@PathVariable("id") String id,  @RequestParam("name") String name,
                                  @RequestParam(value = "image", required = false) MultipartFile image)throws IOException{
        brandService.updateBrand(id, name, image);
        return ApiResponse.<String>builder()
                .result("Cập nhật thương hiệu thành công.")
                .build();
    }

    @PostMapping
    ApiResponse<String> createBrand(@RequestParam("name") String name,
                                  @RequestParam(value = "image", required = false) MultipartFile image)throws IOException{
        brandService.createBrand(name, image);
        return ApiResponse.<String>builder()
                .result("Thêm thương hiệu thành công.")
                .build();
    }

}
