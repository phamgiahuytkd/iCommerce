package com.example.iCommerce.controller;

import com.example.iCommerce.constant.MomoParameter;
import com.example.iCommerce.dto.request.MoMoMethodRequest;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.CreateMomoResponse;
import com.example.iCommerce.service.BrandService;
import com.example.iCommerce.service.MomoService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/momo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MomoController {

    MomoService momoService;


   @PostMapping("/create")
    CreateMomoResponse createMomoQR(@RequestBody MoMoMethodRequest request){
       return momoService.createMomoQR(request);
   }


   @PostMapping("/ipn-handler")
    String ipnHandler(@RequestBody Map<String,String> request){
       return momoService.ipnHandler(request);
   }

}
