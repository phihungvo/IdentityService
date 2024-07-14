package com.luv2code.IdentityService.mapper;

import com.luv2code.IdentityService.dto.request.RoleRequest;
import com.luv2code.IdentityService.dto.response.RoleResponse;
import com.luv2code.IdentityService.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
