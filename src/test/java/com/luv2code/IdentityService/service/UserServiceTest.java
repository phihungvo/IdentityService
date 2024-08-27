package com.luv2code.IdentityService.service;

import com.luv2code.IdentityService.dto.request.UserCreationRequest;
import com.luv2code.IdentityService.dto.response.UserResponse;
import com.luv2code.IdentityService.entity.User;
import com.luv2code.IdentityService.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import static  org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    private UserCreationRequest request;

    private UserResponse userResponse;

    private User user;

    private LocalDate dob;

    @BeforeEach
    public void initData(){
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

        user = User.builder()
                .id("ddf5423798122")
                .username("john")
                .firstname("John")
                .lastname("Doe")
                .dob(dob)
                .build();

    }

    @Test
    public void createUser_validRequest_success(){
        //when

    }

}
