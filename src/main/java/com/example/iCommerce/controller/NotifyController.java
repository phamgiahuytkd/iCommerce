package com.example.iCommerce.controller;

import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.NotifyResponse;
import com.example.iCommerce.service.NotifyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotifyController {

    NotifyService notifyService;

    @GetMapping
    ApiResponse<List<NotifyResponse>> getNotifies(){
        return ApiResponse.<List<NotifyResponse>>builder()
                .result(notifyService.getNotifies())
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteNotifyByType_id(@PathVariable("id") String id){
        notifyService.deleteNotifyByType_id(id);
        return ApiResponse.<String>builder()
                .result("Đã xem thông báo.")
                .build();
    }


}
