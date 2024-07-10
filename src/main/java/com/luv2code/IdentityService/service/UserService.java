package com.luv2code.IdentityService.service;

import com.luv2code.IdentityService.dto.request.ApiResponse;
import com.luv2code.IdentityService.dto.request.UserCreationRequest;
import com.luv2code.IdentityService.dto.request.UserUpdateRequest;
import com.luv2code.IdentityService.dto.response.UserResponse;
import com.luv2code.IdentityService.entity.User;
import com.luv2code.IdentityService.exception.AppException;
import com.luv2code.IdentityService.exception.ErrorCode;
import com.luv2code.IdentityService.mapper.UserMapper;
import com.luv2code.IdentityService.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    public List<UserResponse> getUser(){
        List<UserResponse> userResponse = userMapper.toListUserResponse(userRepository.findAll());
        return userResponse;
    }

    public UserResponse getUser(String userId){
        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    public UserResponse createUser(UserCreationRequest request){

        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUser(user, request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public boolean deleteUser(String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        boolean isDeleted = false;
        try {
            userRepository.deleteById(userId);
        }catch (Exception e){
        }
        return isDeleted = true;
    }
}
