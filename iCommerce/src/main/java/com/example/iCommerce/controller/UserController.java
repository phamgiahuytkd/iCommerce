package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.UserCreationRequest;
import com.example.iCommerce.dto.request.UserUpdateRequest;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;


    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody  UserCreationRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @PutMapping("/{userID}")
    ApiResponse<UserResponse> updateUser(@PathVariable("userID") String userID, @RequestBody UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder()
                .result( userService.updateUser(userID, request))
                .build();

    }


    @DeleteMapping("/{userID}")
    ApiResponse<String> deleteUser(@PathVariable String userID){
        userService.deleteUser(userID);
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



    @GetMapping
    ApiResponse<List<UserResponse>> getUsers(){

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }



}
