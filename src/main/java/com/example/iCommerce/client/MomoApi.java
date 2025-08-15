package com.example.iCommerce.client;


import com.example.iCommerce.dto.request.CreateMomoRequest;
import com.example.iCommerce.dto.response.CreateMomoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "momo", url = "https://test-payment.momo.vn/v2/gateway/api/create")
public interface MomoApi {
    @PostMapping
//            ("/create")
    CreateMomoResponse createMomoQR(@RequestBody CreateMomoRequest createMomoRequest);
}
