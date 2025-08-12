package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.UserRequest;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.UserAdminResponse;
import com.example.iCommerce.dto.response.UserLoggedResponse;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    @PutMapping
    ApiResponse<UserResponse> updateUser(@RequestBody UserRequest request){
        return ApiResponse.<UserResponse>builder()
                .result( userService.updateUser(request))
                .build();

    }

    @PutMapping("/avatar")
    ApiResponse<String> updateUserAvatar(@RequestParam(value = "image") MultipartFile image) throws IOException {
        userService.updateUserAvatar(image);
        return ApiResponse.<String>builder()
                .result("SUCCEED")
                .build();

    }



//    @GetMapping("/{userID}")
//    ApiResponse<UserResponse> getUser(@PathVariable("userID") String userID){
//
//        return ApiResponse.<UserResponse>builder()
//                .result(userService.getUser(userID))
//                .build();
//    }



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

}
