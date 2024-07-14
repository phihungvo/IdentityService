package com.luv2code.IdentityService.service;

import com.luv2code.IdentityService.dto.request.RoleRequest;
import com.luv2code.IdentityService.dto.response.RoleResponse;
import com.luv2code.IdentityService.mapper.RoleMapper;
import com.luv2code.IdentityService.repository.PermissionRepository;
import com.luv2code.IdentityService.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request){
        var role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }


    public List<RoleResponse> getAll(){
        // var roles = roleRepository.findAll();
        // return roles.stream().map(roleMapper::toRoleResponse).toList();
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    public void delete(String role){
        roleRepository.deleteById(role);
    }
}
