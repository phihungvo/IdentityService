package com.luv2code.IdentityService.exception;

import com.luv2code.IdentityService.dto.request.ApiResponse;
import com.luv2code.IdentityService.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse> runtimeHandler(RuntimeException exception){
        ApiResponse<ErrorCode> apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse> appExceptionHandler(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatusCode()).body(
                        ApiResponse.builder()
                                .code(errorCode.getCode())
                                .message(errorCode.getMessage())
                                .build()
                );
    }


    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception){
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode()).body(
                        ApiResponse.builder()
                                .code(errorCode.getCode())
                                .message(errorCode.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> validationExceptionHandler(MethodArgumentNotValidException exception){
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try{
            errorCode = ErrorCode.valueOf(enumKey); //Bọc đối tượng enumKey kiểu String sang dạng ErrorCode
        }catch (IllegalArgumentException e){
            //Nếu không có giá trị của enumKey trong enum thì ngoại lệ Illegal xảy ra
        }
        ApiResponse<ErrorCode> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }


}
