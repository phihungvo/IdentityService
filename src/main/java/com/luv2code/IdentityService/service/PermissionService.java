package com.luv2code.IdentityService.service;

import com.luv2code.IdentityService.dto.request.PermissionRequest;
import com.luv2code.IdentityService.dto.response.PermissionResponse;
import com.luv2code.IdentityService.entity.Permission;
import com.luv2code.IdentityService.mapper.PermissionMapper;
import com.luv2code.IdentityService.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request){
        return permissionMapper.toPermissionResponse(
                permissionRepository.save(
                        permissionMapper.toPermission(request)));
    }

    public List<PermissionResponse> getAll(){
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permissionName){
        permissionRepository.deleteById(permissionName);
    }
}
