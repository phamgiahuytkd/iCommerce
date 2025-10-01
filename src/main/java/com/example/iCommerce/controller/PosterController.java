package com.example.iCommerce.controller;

import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.PosterResponse;
import com.example.iCommerce.service.BrandService;
import com.example.iCommerce.service.PosterService;
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
@RequestMapping("/poster")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PosterController {

    PosterService posterService;

    @GetMapping
    ApiResponse<List<PosterResponse>> getPosters(){
        return ApiResponse.<List<PosterResponse>>builder()
                .result(posterService.getPosters())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<PosterResponse> getPoster(@PathVariable("id") String id){
        return ApiResponse.<PosterResponse>builder()
                .result(posterService.getPoster(id))
                .build();
    }

    @PostMapping
    ApiResponse<String> createPoster(@RequestParam("link") String link,
                                      @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        posterService.createPoster(link, image);
        return ApiResponse.<String>builder()
                .result("Thêm áp phích thành công.")
                .build();
    }


    @PutMapping("/{id}")
    ApiResponse<String> updatePoster(@PathVariable("id") String id, @RequestParam("link") String link,
                                     @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        posterService.updatePoster(id, link, image);
        return ApiResponse.<String>builder()
                .result("Cập nhật áp phích thành công.")
                .build();
    }


    @DeleteMapping("/{id}")
    ApiResponse<String> deletePoster(@PathVariable String id) throws IOException {
        posterService.deletePoster(id);
        return ApiResponse.<String>builder()
                .result("Đã xóa áp phích thành công.")
                .build();
    }




}
