package com.luv2code.IdentityService.mapper;

import com.luv2code.IdentityService.dto.request.UserCreationRequest;
import com.luv2code.IdentityService.dto.request.UserUpdateRequest;
import com.luv2code.IdentityService.dto.response.UserResponse;
import com.luv2code.IdentityService.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
    List<UserResponse> toListUserResponse(List<User> listUser);


    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
