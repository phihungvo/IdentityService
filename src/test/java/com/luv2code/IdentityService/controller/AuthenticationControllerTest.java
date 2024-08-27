package com.luv2code.IdentityService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luv2code.IdentityService.dto.request.AuthenticationRequest;
import com.luv2code.IdentityService.dto.request.IntrospectRequest;
import com.luv2code.IdentityService.dto.request.LogoutRequest;
import com.luv2code.IdentityService.dto.request.RefreshRequest;
import com.luv2code.IdentityService.dto.response.AuthenticationResponse;
import com.luv2code.IdentityService.dto.response.IntrospectResponse;
import com.luv2code.IdentityService.exception.AppException;
import com.luv2code.IdentityService.exception.ErrorCode;
import com.luv2code.IdentityService.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    private AuthenticationRequest authRequest;

    private AuthenticationResponse authResponse;

    private IntrospectRequest introspectRequest;

    private IntrospectResponse introspectResponse;

    private RefreshRequest refreshRequest;

    private LogoutRequest logoutRequest;

    @BeforeEach
    public void initData() {
        // Authentication request and response initialization
        authRequest = AuthenticationRequest.builder()
                .username("john")
                .password("12345678")
                .build();

        authResponse = AuthenticationResponse.builder()
                .token("validToken")
                .authenticated(true)
                .build();

        // Introspect request and response initialization
        introspectRequest = new IntrospectRequest();
        introspectRequest.setToken("validToken");

        introspectResponse = IntrospectResponse.builder()
                .valid(true)
                .build();

        // Refresh request and response initialization
        refreshRequest = new RefreshRequest();
        refreshRequest.setToken("validRefreshToken");

        authResponse = AuthenticationResponse.builder()
                .token("newToken")
                .authenticated(true)
                .build();

        // Logout request initialization
        logoutRequest = new LogoutRequest();
        logoutRequest.setToken("validToken");
    }

    @Test
    public void authenticated_validRequest_success() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("john");
        request.setPassword("password123");

        AuthenticationResponse response = AuthenticationResponse.builder()
                .token("generatedToken")
                .authenticated(true)
                .build();

        Mockito.when(authenticationService.authenticate(ArgumentMatchers.any(AuthenticationRequest.class)))
                .thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.authenticated").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.token").value("generatedToken"));
    }

    @Test
    public void authenticated_invalidPassword_fail() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("john");
        request.setPassword("wrongPassword");

        Mockito.when(authenticationService.authenticate(ArgumentMatchers.any(AuthenticationRequest.class)))
                .thenThrow(new AppException(ErrorCode.UNAUTHENTICATED));

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
    @Test
    public void introspect_validToken_success() throws Exception {
        // Given
        IntrospectRequest request = new IntrospectRequest();
        request.setToken("validToken");

        IntrospectResponse response = IntrospectResponse.builder()
                .valid(true)
                .build();

        Mockito.when(authenticationService.introspect(ArgumentMatchers.any(IntrospectRequest.class)))
                .thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.valid").value(true));
    }
    @Test
    public void refreshToken_validToken_success() throws Exception {
        // Given
        RefreshRequest request = new RefreshRequest();
        request.setToken("validRefreshToken");

        AuthenticationResponse response = AuthenticationResponse.builder()
                .token("newToken")
                .authenticated(true)
                .build();

        Mockito.when(authenticationService.refreshToken(ArgumentMatchers.any(RefreshRequest.class)))
                .thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.token").value("newToken"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.authenticated").value(true));
    }
    @Test
    public void logout_validToken_success() throws Exception {
        // Given
        LogoutRequest request = new LogoutRequest();
        request.setToken("validToken");

        Mockito.doNothing().when(authenticationService).logout(ArgumentMatchers.any(LogoutRequest.class));

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
