package com.luv2code.IdentityService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luv2code.IdentityService.dto.request.RoleRequest;
import com.luv2code.IdentityService.dto.request.UserCreationRequest;
import com.luv2code.IdentityService.dto.response.PermissionResponse;
import com.luv2code.IdentityService.dto.response.RoleResponse;
import com.luv2code.IdentityService.dto.response.UserResponse;
import com.luv2code.IdentityService.entity.User;
import com.luv2code.IdentityService.exception.AppException;
import com.luv2code.IdentityService.exception.ErrorCode;
import com.luv2code.IdentityService.mapper.UserMapper;
import com.luv2code.IdentityService.repository.RoleRepository;
import com.luv2code.IdentityService.repository.UserRepository;
import com.luv2code.IdentityService.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Optional;

import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
import static jdk.jfr.internal.jfc.model.Constraint.any;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RoleRepository roleRepository;

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
        Mockito.when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        Mockito.when(userMapper.toUser(request)).thenReturn(user);
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.createUser(request);

        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userRepository).save(user);
    }

    @Test
    public void createUser_existingUsername_throwsException(){
        Mockito.when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThrows(AppException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void getUser_existingUserId_returnsUser(){
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUser(user.getId());

        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userRepository).findById(user.getId());
    }

    @Test
    public void getUser_nonExistingUserId_throwsException(){
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> userService.getUser(user.getId()));
        verify(userRepository).findById(user.getId());
    }

    @Test
    public void deleteUser_existingUserId_success(){
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        boolean result = userService.deleteUser(user.getId());

        assertTrue(result);
        verify(userRepository).deleteById(user.getId());
    }

    @Test
    public void deleteUser_nonExistingUserId_throwsException(){
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> userService.deleteUser(user.getId()));
        verify(userRepository, never()).deleteById(user.getId());
    }

    @Test
    public void updateUser_validRequest_success(){
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUser(user.getId(), request);

        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userRepository).save(user);
    }

    @Test
    public void updateUser_nonExistingUserId_throwsException(){
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> userService.updateUser(user.getId(), request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void getMyInfo_authenticatedUser_success(){
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(user.getUsername());
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getMyInfo();

        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    public void getMyInfo_nonExistingUser_throwsException(){
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(user.getUsername());
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> userService.getMyInfo());
        verify(userRepository).findByUsername(user.getUsername());
    }
}
