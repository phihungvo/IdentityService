package com.luv2code.IdentityService.service;

import com.luv2code.IdentityService.dto.request.RoleRequest;
import com.luv2code.IdentityService.dto.response.RoleResponse;
import com.luv2code.IdentityService.entity.Permission;
import com.luv2code.IdentityService.entity.Role;
import com.luv2code.IdentityService.mapper.RoleMapper;
import com.luv2code.IdentityService.repository.PermissionRepository;
import com.luv2code.IdentityService.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private RoleRequest roleRequest;
    private Role role;
    private RoleResponse roleResponse;
    private Set<Permission> permissions;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        permissions = new HashSet<>();
        permissions.add(new Permission("READ_PRIVILEGES"));
        permissions.add(new Permission("WRITE_PRIVILEGES"));

        roleRequest = new RoleRequest("ADMIN", List.of("READ_PRIVILEGES", "WRITE_PRIVILEGES"));
        role = new Role("ADMIN", permissions);
        roleResponse = new RoleResponse("ADMIN", List.of("READ_PRIVILEGES", "WRITE_PRIVILEGES"));
    }

    @Test
    public void testCreateRole() {
        when(roleMapper.toRole(roleRequest)).thenReturn(role);
        when(permissionRepository.findAllById(roleRequest.getPermissions())).thenReturn(permissions);
        when(roleRepository.save(role)).thenReturn(role);
        when(roleMapper.toRoleResponse(role)).thenReturn(roleResponse);

        RoleResponse result = roleService.create(roleRequest);

        assertNotNull(result);
        assertEquals("ADMIN", result.getName());
        assertEquals(2, result.getPermissions().size());

        verify(roleMapper, times(1)).toRole(roleRequest);
        verify(permissionRepository, times(1)).findAllById(roleRequest.getPermissions());
        verify(roleRepository, times(1)).save(role);
        verify(roleMapper, times(1)).toRoleResponse(role);
    }

    @Test
    public void testGetAllRoles() {
        List<Role> roles = List.of(role);
        List<RoleResponse> roleResponses = List.of(roleResponse);

        when(roleRepository.findAll()).thenReturn(roles);
        when(roleMapper.toRoleResponse(role)).thenReturn(roleResponse);

        List<RoleResponse> result = roleService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getName());

        verify(roleRepository, times(1)).findAll();
        verify(roleMapper, times(1)).toRoleResponse(role);
    }

    @Test
    public void testDeleteRole() {
        String roleName = "ADMIN";

        doNothing().when(roleRepository).deleteById(roleName);

        roleService.delete(roleName);

        verify(roleRepository, times(1)).deleteById(roleName);
    }

    @Test
    public void testGetAllRoles_Success() {
        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(roleMapper.toRoleResponse(any(Role.class))).thenReturn(roleResponse);

        List<RoleResponse> result = roleService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(roleResponse.getName(), result.get(0).getName());
    }

    @Test
    public void testDeleteRole_Success() {
        doNothing().when(roleRepository).deleteById(anyString());

        roleService.delete("ROLE_USER");

        verify(roleRepository, times(1)).deleteById(anyString());
    }

    @Test
    public void testCreateRole_EmptyPermissions() {
        roleRequest.setPermissions(Collections.emptySet());

        when(roleMapper.toRole(any(RoleRequest.class))).thenReturn(role);
        when(permissionRepository.findAllById(anySet())).thenReturn(Collections.emptySet());
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toRoleResponse(any(Role.class))).thenReturn(roleResponse);

        RoleResponse result = roleService.create(roleRequest);

        assertNotNull(result);
        assertTrue(result.getPermissions().isEmpty());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    public void testCreateRole_PermissionNotFound() {
        when(roleMapper.toRole(any(RoleRequest.class))).thenReturn(role);
        when(permissionRepository.findAllById(anySet())).thenReturn(Collections.emptySet());

        RoleResponse result = roleService.create(roleRequest);

        assertNotNull(result);
        assertTrue(result.getPermissions().isEmpty());
        verify(roleRepository, times(1)).save(any(Role.class));
    }
}

