package com.luv2code.IdentityService.controller;

import com.luv2code.IdentityService.dto.request.ApiResponse;
import com.luv2code.IdentityService.dto.request.AuthenticationRequest;
import com.luv2code.IdentityService.dto.response.AuthenticationResponse;
import com.luv2code.IdentityService.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/log-in")
    public ApiResponse<AuthenticationResponse> authenticated(@RequestBody AuthenticationRequest request){
        boolean result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .results(AuthenticationResponse.builder()
                        .authenticated(result)
                        .build())
                .build();
    }

}
