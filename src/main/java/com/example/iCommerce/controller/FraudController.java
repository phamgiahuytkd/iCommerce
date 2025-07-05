package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.FraudRequest;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.FraudResponse;
import com.example.iCommerce.service.FraudService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/fraud")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FraudController {

    FraudService fraudService;


    @PostMapping("/predict")
    ApiResponse<FraudResponse>  predict(@RequestBody FraudRequest request) {
        log.debug("Received request: " + request);
        return ApiResponse.<FraudResponse>builder()
                .result(fraudService.predictFraud(request))
                .build();

    }
}
