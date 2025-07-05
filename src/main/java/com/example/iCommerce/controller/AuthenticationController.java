package com.example.iCommerce.controller;

import com.example.iCommerce.dto.request.*;
import com.example.iCommerce.dto.response.ApiResponse;
import com.example.iCommerce.dto.response.AuthenticationResponse;
import com.example.iCommerce.dto.response.IntrospectResponse;
import com.example.iCommerce.service.AuthenticationService;
import com.example.iCommerce.service.ResetPasswordService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    ResetPasswordService resetService;


    @PostMapping("/google-login")
    public ApiResponse<AuthenticationResponse> googleLogin(@RequestBody SocialLoginRequest request) {
        AuthenticationResponse response = authenticationService.authenticateSocialLogin(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(response)
                .build();
    }


    @PostMapping("/facebook-login")
    public ApiResponse<AuthenticationResponse> facebookLogin(@RequestBody SocialLoginRequest request) {
        AuthenticationResponse response = authenticationService.authenticateSocialLogin(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(response)
                .build();
    }


    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }


    @PostMapping("/logout")
    ApiResponse<String> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<String>builder()
                .result("Thành công")
                .build();
    }


    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }



    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/change-password")
    ApiResponse<String> postChangePassWord(@RequestBody ChangePassWordRequest request){
        authenticationService.changePassWord(request);
        return ApiResponse.<String>builder()
                .result("Cập nhật mật khẩu thành công.")
                .build();
    }





    ///////Reset Password
    @PostMapping("/forgot-password")
    ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        resetService.requestReset(email);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Mã xác nhận đã được gửi.")
                .build());
    }

    @PostMapping("/verify-reset-token")
    ResponseEntity<ApiResponse<Void>> verifyToken(
            @RequestParam String email,
            @RequestParam String token
    ) {
        boolean isValid = resetService.verifyToken(email, token);
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .code(200)
                    .message("Mã xác nhận hợp lệ.")
                    .build());
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .code(400)
                    .message("Mã xác nhận sai hoặc đã hết hạn.")
                    .build());
        }
    }

    @PostMapping("/reset-password")
    ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String email,
            @RequestParam String token,
            @RequestParam String newPassword
    ) {
        resetService.resetPassword(email, token, newPassword);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Mật khẩu đã được đặt lại.")
                .build());
    }




}
