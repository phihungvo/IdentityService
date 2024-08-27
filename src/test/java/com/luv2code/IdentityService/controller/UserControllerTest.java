package com.luv2code.IdentityService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.luv2code.IdentityService.dto.request.UserCreationRequest;
import com.luv2code.IdentityService.dto.request.UserUpdateRequest;
import com.luv2code.IdentityService.dto.response.UserResponse;
import com.luv2code.IdentityService.exception.AppException;
import com.luv2code.IdentityService.exception.ErrorCode;
import com.luv2code.IdentityService.service.UserService;
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

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserCreationRequest request;

    private UserResponse userResponse;

    private LocalDate dob;

    @BeforeEach
    public void initData() {
        dob = LocalDate.of(1999, 1, 1);

        request = UserCreationRequest.builder()
                .username("john")
                .firstname("John")
                .lastname("Doe")
                .password("12345678")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id("ddf5423798122")
                .username("john")
                .firstname("John")
                .lastname("Doe")
                .dob(dob)
                .build();
    }

    @Test
    public void createUser_validRequest_success() throws Exception {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(userResponse);

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0));
    }

    @Test
    public void createUser_usernameInvalid_fail() throws Exception {
        // Given
        request.setUsername("j");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value(1002))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Username must be at least 4 characters"));
    }

    // Additional test cases

    @Test
    public void createUser_duplicateUsername_fail() throws Exception {
        // Given
        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenThrow(new AppException(ErrorCode.USER_EXISTED));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1001))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("User already exists"));
    }

    @Test
    public void getUserById_validId_success() throws Exception {
        // Given
        String userId = "ddf5423798122";
        Mockito.when(userService.getUser(userId)).thenReturn(userResponse);

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.username").value("john"));
    }

    @Test
    public void getUserById_invalidId_fail() throws Exception {
        // Given
        String userId = "invalidId";
        Mockito.when(userService.getUser(userId)).thenThrow(new AppException(ErrorCode.USER_NOT_FOUND));

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("User not found"));
    }

    @Test
    public void updateUser_validRequest_success() throws Exception {
        // Given
        String userId = "ddf5423798122";
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .firstname("Johnny")
                .lastname("Doe")
                .password("newpassword")
                .build();

        Mockito.when(userService.updateUser(ArgumentMatchers.eq(userId), ArgumentMatchers.any())).thenReturn(userResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.firstname").value("Johnny"));
    }

    @Test
    public void deleteUser_validId_success() throws Exception {
        // Given
        String userId = "ddf5423798122";
        Mockito.when(userService.deleteUser(userId)).thenReturn(true);

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results").value(true));
    }
}
