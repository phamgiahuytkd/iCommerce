package com.example.iCommerce.controller;

import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.GiftResponse;
import com.example.iCommerce.service.BrandService;
import com.example.iCommerce.service.GiftService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/gift")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GiftController {

    GiftService giftService;

    @PostMapping
    ApiResponse<List<GiftResponse>> getGifts(@RequestBody List<String> productIds){
        return ApiResponse.<List<GiftResponse>>builder()
                .result(giftService.getGifts(productIds))
                .build();
    }


    @GetMapping("/{id}/product-variant")
    ApiResponse<List<GiftResponse>> getGiftsProductVariant(@PathVariable("id") String id){
        return ApiResponse.<List<GiftResponse>>builder()
                .result(giftService.getGiftsProductVariant(id))
                .build();
    }






}
