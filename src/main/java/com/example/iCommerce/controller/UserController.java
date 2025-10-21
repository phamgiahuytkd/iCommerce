package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.UserRequest;
import com.example.iCommerce.dto.response.*;
import com.example.iCommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;


    @PostMapping
    ApiResponse<UserResponse> createUser(@Valid @RequestBody UserRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UserResponse> updateUser(@ModelAttribute UserRequest request) throws IOException {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(request))
                .build();

    }

    @PutMapping("/avatar")
    ApiResponse<String> updateUserAvatar(@RequestParam(value = "image") MultipartFile image) throws IOException {
        userService.updateUserAvatar(image);
        return ApiResponse.<String>builder()
                .result("SUCCEED")
                .build();

    }



    @GetMapping("/{userID}")
    ApiResponse<UserResponse> getUser(@PathVariable("userID") String userID){

        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userID))
                .build();
    }



    @GetMapping("/info")
    ApiResponse<UserResponse> getMyInfo(){

        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @GetMapping("/logged")
    ApiResponse<UserLoggedResponse> getUserLogged(){

        return ApiResponse.<UserLoggedResponse>builder()
                .result(userService.getUserLogged())
                .build();
    }



    @GetMapping
    ApiResponse<List<UserAdminResponse>> getUsers(){

        return ApiResponse.<List<UserAdminResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

/// //////ADMIN////// ///

    @PutMapping("/{id}/block")
    ApiResponse<String> blockUser(@PathVariable("id") String id){
        userService.blockUser(id);
        return ApiResponse.<String>builder()
                .result("Đã khóa tài khoản.")
                .build();

    }

    @PutMapping("/{id}/unblock")
    ApiResponse<String> unblockUser(@PathVariable("id") String id){
        userService.unblockUser(id);
        return ApiResponse.<String>builder()
                .result("Đã mở khóa tài khoản.")
                .build();

    }


    ///////Customer//////
    @GetMapping("/{userID}/overview")
    ApiResponse<UserOverviewResponse> getUserOverview(@PathVariable("userID") String userID){

        return ApiResponse.<UserOverviewResponse>builder()
                .result(userService.getUserOverview(userID))
                .build();
    }

    @GetMapping("/{userID}/recent-orders")
    ApiResponse<List<OrderResponse>> getOrders(@PathVariable("userID") String userID){

        return ApiResponse.<List<OrderResponse>>builder()
                .result(userService.getRecentOrdersUser(userID))
                .build();
    }


    @GetMapping("/{userID}/top-user-product")
    ApiResponse<List<Object[]>> getTopUserProducts(@PathVariable("userID") String userID){

        return ApiResponse.<List<Object[]>>builder()
                .result(userService.getTopUserProducts(userID))
                .build();
    }


    @GetMapping("/{userID}/top-user-gift")
    ApiResponse<List<Object[]>> getTopUserGifts(@PathVariable("userID") String userID){

        return ApiResponse.<List<Object[]>>builder()
                .result(userService.getTopUserGifts(userID))
                .build();
    }


}
