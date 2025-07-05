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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
