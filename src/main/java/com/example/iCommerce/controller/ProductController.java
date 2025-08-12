package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.CartRequest;
import com.example.iCommerce.dto.request.LoveProductRequest;
import com.example.iCommerce.dto.request.ProductRequest;
import com.example.iCommerce.dto.request.SearchProductRequest;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.PriceRangeResponse;
import com.example.iCommerce.dto.response.ProductAdminResponse;
import com.example.iCommerce.dto.response.ProductResponse;
import com.example.iCommerce.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> createProduct(
            @ModelAttribute ProductRequest request) throws Exception {
        System.out.println("✅ " + request.getName());
        System.out.println("✅ Số biến thể: " + request.getVariants().size());
        productService.createProduct(request);
        return ApiResponse.<String>builder()
                .result("Đã thêm sản phẩm!")
                .build();
    }



    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<String> updateProduct(@PathVariable("id") String id, @ModelAttribute ProductRequest request) throws IOException {
        productService.updateProduct(id, request);
        return ApiResponse.<String>builder()
                .result("Cập nhật thành công")
                .build();

    }




    @DeleteMapping("/{id}")
    ApiResponse<String> deleteProduct(@PathVariable String id){
        productService.deleteProduct(id);
        return ApiResponse.<String>builder()
                .result("Đã xóa sản phẩm!")
                .build();
    }


    @GetMapping("/{id}")
    ApiResponse<ProductResponse> getProduct(@PathVariable("id") String id){

        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProduct(id))
                .build();
    }




    @GetMapping
    ApiResponse<List<ProductResponse>> getProducts(){

        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getProducts())
                .build();
    }



    @GetMapping("/{category}/{brand}")
    ApiResponse<List<ProductResponse>> getGroupProduct(@PathVariable String category_id, @PathVariable String brand_id){

        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getGroupProduct(category_id, brand_id))
                .build();
    }



    @PostMapping("/filter")
    ApiResponse<List<ProductResponse>> getFilterProduct(@RequestBody SearchProductRequest request){
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getFilterProduct(request))
                .build();
    }

    @PostMapping("/search")
    ApiResponse<List<ProductResponse>> getSearchProduct(@RequestBody SearchProductRequest request){
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getSearchProduct(request))
                .build();
    }



    @GetMapping("/top10discount")
    ApiResponse<List<ProductResponse>> getTop10ProductDiscount(){

        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getTop10ProductDiscount())
                .build();
    }

    @GetMapping("/latest")
    ApiResponse<List<ProductResponse>> getLatestProducts(){

        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getLatestProducts())
                .build();
    }


    @PostMapping("/loveproduct")
    ApiResponse<String> createLoveProduct(@RequestBody LoveProductRequest request){
        productService.createLoveProduct(request);
        return ApiResponse.<String>builder()
                .result("Đã thêm sản phẩm vào yêu thích!")
                .build();
    }

    @DeleteMapping("/loveproduct")
    ApiResponse<String> deleteLoveProduct(@RequestBody LoveProductRequest request){
        productService.deleteLoveProduct(request);
        return ApiResponse.<String>builder()
                .result("Đã xóa sản phẩm khỏi yêu thích!")
                .build();
    }

    @GetMapping("/loveproduct")
    ApiResponse<List<ProductResponse>> getLoveProducts(){

        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getLoveProducts())
                .build();
    }



    /// admin ///
    @GetMapping("/{id}/admin")
    ApiResponse<ProductAdminResponse> getProductAdmin(@PathVariable("id") String id){

        return ApiResponse.<ProductAdminResponse>builder()
                .result(productService.getProductAdmin(id))
                .build();
    }




}
