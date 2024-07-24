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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

        //private static final Logger log = LoggerFactory.getLogger(UserController.class);
        UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
            return ApiResponse.<UserResponse>builder()
            .results(userService.createUser(request))
            .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getUser(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

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

    @GetMapping("/getInfo")
    public ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder()
                .results(userService.getMyInfo())
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
