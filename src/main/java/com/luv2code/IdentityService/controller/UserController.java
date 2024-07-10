package com.luv2code.IdentityService.controller;

import com.luv2code.IdentityService.dto.request.ApiResponse;
import com.luv2code.IdentityService.dto.request.UserCreationRequest;
import com.luv2code.IdentityService.dto.request.UserUpdateRequest;
import com.luv2code.IdentityService.dto.response.UserResponse;
import com.luv2code.IdentityService.service.UserService;
import jakarta.validation.Valid;
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
        public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
            return ApiResponse.<UserResponse>builder()
                    .results(userService.createUser(request))
                    .build();
        }

    @GetMapping
    public ApiResponse<List<UserResponse>> getUser(){
        return ApiResponse.<List<UserResponse>>builder()
                .results(userService.getUser())
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable String userId){
        return ApiResponse.<UserResponse>builder()
                .results(userService.getUser(userId))
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder()
                .results(userService.updateUser(userId, request))
                .build();
    }


    @DeleteMapping("/{userId}")
    public ApiResponse<Boolean> deleteUser(@PathVariable String userId){
        return ApiResponse.<Boolean>builder()
                .results(userService.deleteUser(userId))
                .build();
    }
}
