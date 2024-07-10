package com.luv2code.IdentityService.controller;

import com.luv2code.IdentityService.dto.request.ApiResponse;
import com.luv2code.IdentityService.dto.request.AuthenticationRequest;
import com.luv2code.IdentityService.dto.request.IntrospectRequest;
import com.luv2code.IdentityService.dto.response.AuthenticationResponse;
import com.luv2code.IdentityService.dto.response.IntrospectResponse;
import com.luv2code.IdentityService.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticated(@RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .results(result)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> authenticated(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .results(result)
                .build();
    }
}
