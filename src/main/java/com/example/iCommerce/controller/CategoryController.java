package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.CartRequest;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.dto.response.CategoryResponse;
import com.example.iCommerce.service.CartService;
import com.example.iCommerce.service.CategoryService;
import com.example.iCommerce.service.OrderService;
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
@RequestMapping("/category")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    @GetMapping
    ApiResponse<List<CategoryResponse>> getCategories(){
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getCategories())
                .build();
    }


    @GetMapping("/{id}")
    ApiResponse<CategoryResponse> getCategory(@PathVariable("id") String id){
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.getCategory(id))
                .build();
    }


    @PutMapping("/{id}")
    ApiResponse<String> updateCategory(@PathVariable("id") String id,  @RequestParam("name") String name,
                                    @RequestParam(value = "image", required = false) MultipartFile image)throws IOException {
        categoryService.updateCategory(id, name, image);
        return ApiResponse.<String>builder()
                .result("Cập nhật danh mục thành công.")
                .build();
    }

    @PostMapping
    ApiResponse<String> createCategory(@RequestParam("name") String name,
                                    @RequestParam(value = "image", required = false) MultipartFile image)throws IOException{
        categoryService.createCategory(name, image);
        return ApiResponse.<String>builder()
                .result("Thêm danh mục thành công.")
                .build();
    }



}
