package com.luv2code.IdentityService.service;

import com.luv2code.IdentityService.dto.request.AuthenticationRequest;
import com.luv2code.IdentityService.entity.User;
import com.luv2code.IdentityService.exception.AppException;
import com.luv2code.IdentityService.exception.ErrorCode;
import com.luv2code.IdentityService.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;

    public boolean authenticate(AuthenticationRequest request){
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        return passwordEncoder.matches(request.getPassword(), user.getPassword());
    }
}
