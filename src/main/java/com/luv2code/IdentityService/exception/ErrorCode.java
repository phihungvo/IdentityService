package com.luv2code.IdentityService.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;



@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception"),
    INVALID_KEY(1000, "Invalid key"),
    USER_EXISTED(1001,"User existed"),
    USERNAME_INVALID(1002, "Username must be at least 3 characters"),
    PASSWORD_INVALID(1003, "Password must be at least 8 characters"),
    USER_NOT_FOUND(1004, "User not found")
    ;

    int code;
    String message;

    ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }
}
