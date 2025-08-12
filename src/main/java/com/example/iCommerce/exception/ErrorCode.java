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
    EMAIL_NOT_EXISTED(1003, "Email chưa đăng ký.", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT(1004, "Mật khẩu không đúng", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1005, "Mật khẩu phải trên 8 ký tự. Bao gồm 1 chữ cái viết hoa, 1 ký tự đặc biệt", HttpStatus.BAD_REQUEST),
    NOT_VALUE(1006, "Không được để trống trường này", HttpStatus.BAD_REQUEST),
    NO_ADDRESS(1007, "Vui lòng thêm địa chỉ giao hàng", HttpStatus.BAD_REQUEST),
    NO_PHONE_NUMBER(1008, "Vui lòng thêm số điện thoại giao hàng", HttpStatus.BAD_REQUEST),
    MAX_SIZE_FILE(1009, "Kích thuớc tệp quá giới hạn", HttpStatus.BAD_REQUEST),
    INVALID_DATE_OF_BIRTH(10011, "Ngày sinh không hợp lệ.", HttpStatus.BAD_REQUEST),
    INVALID_STOCK(10012, "Số lượng kho không hợp lệ.", HttpStatus.BAD_REQUEST),
    INVALID_DATE_RANGE(10013, "Khoảng thời gian không hợp lệ.", HttpStatus.BAD_REQUEST),




    PRODUCT_EXISTED(3001, "Sản phẩm đã tồn tại", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(3002, "Sản phẩm không tồn tại", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_ENOUGH(3012, "Sản phẩm không đủ dố lượng", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXISTED(3003, "Danh mục không tồn tại", HttpStatus.BAD_REQUEST),
    BRAND_NOT_EXISTED(3004, "Danh mục không tồn tại", HttpStatus.BAD_REQUEST),
    CAN_NOT_DELETE_PRODUCT(3005, "Không thể xóa sản phẩm", HttpStatus.BAD_REQUEST),
    GIFT_ALREADY_EXISTS(3007, "Quà tặng đã tồn tại", HttpStatus.BAD_REQUEST),
    GIFT_NOT_EXISTED(3006, "Quà không tồn tại", HttpStatus.BAD_REQUEST),
    ATTRIBUTE_EXISTED(3021, "Thuộc tính đã tồn tại", HttpStatus.BAD_REQUEST),
    ATTRIBUTE_NOT_EXISTED(3022, "Thuộc tính không tồn tại", HttpStatus.BAD_REQUEST),
    ATTRIBUTE_VALUE_NOT_EXISTED(3032, "Giá trị thuộc tính không tồn tại", HttpStatus.BAD_REQUEST),

    IS_SOCIAL_ACCOUNT(4001, "Không khả dụng với tài khoản mạng xã hội.", HttpStatus.BAD_REQUEST),



    INVALID_IMAGE(9001, "Ảnh không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_FORMAT(9002, "Không đúng định dạng ảnh", HttpStatus.BAD_REQUEST),













    USERNAME_INVALID(1005, "User name must be at least {min} character", HttpStatus.BAD_REQUEST),
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
