package com.luv2code.IdentityService.mapper;

import com.luv2code.IdentityService.dto.request.PermissionRequest;
import com.luv2code.IdentityService.dto.response.PermissionResponse;
import com.luv2code.IdentityService.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
