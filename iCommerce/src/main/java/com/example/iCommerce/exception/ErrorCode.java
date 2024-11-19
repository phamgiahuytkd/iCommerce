package com.example.iCommerce.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "UNCATEGORIZED EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR),
    KEY_INVALID(1000, "INVALID KEY MESSAGE", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1001, "Tài khoản không tồn tại", HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(1002, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_EXISTED(1003, "Email chưa đăng ký", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT(1004, "Mật khẩu không đúng", HttpStatus.BAD_REQUEST),
    PRODUCT_EXISTED(3001, "Sản phẩm đã tồn tại", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(3002, "Sản phẩm không tồn tại", HttpStatus.BAD_REQUEST),













    USERNAME_INVALID(1005, "User name must be at least {min} character", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1006, "Password must be at least {min} character", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(2001, "Lỗi truy cập", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2002, "You don't have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age have to be at least {min}", HttpStatus.BAD_REQUEST),
    ;



    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
