package com.luv2code.IdentityService.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1000, "Invalid key", HttpStatus.BAD_GATEWAY),
    USER_EXISTED(1001,"User existed", HttpStatus.BAD_GATEWAY),
    USERNAME_INVALID(1002, "Username must be at least 3 characters", HttpStatus.BAD_GATEWAY),
    PASSWORD_INVALID(1003, "Password must be at least 8 characters", HttpStatus.BAD_GATEWAY),
    USER_NOT_FOUND(1004, "User not found", HttpStatus.NOT_FOUND),
    USER_HAS_BEEN_DELETED(1005, "User has been deleted", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1006, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "You don't have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1009, "Invalid Date of Birth", HttpStatus.BAD_REQUEST)
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode){
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
