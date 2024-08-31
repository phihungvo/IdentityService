package com.luv2code.IdentityService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luv2code.IdentityService.dto.request.RoleRequest;
import com.luv2code.IdentityService.dto.response.PermissionResponse;
import com.luv2code.IdentityService.dto.response.RoleResponse;
import com.luv2code.IdentityService.exception.AppException;
import com.luv2code.IdentityService.exception.ErrorCode;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    private RoleRequest roleRequest;
    private RoleResponse roleResponse;

    @BeforeEach
    public void setup() {
        roleRequest = RoleRequest.builder()
                .name("ROLE_ADMIN")
                .description("Administrator role")
                .permissions(Set.of("CREATE_USER", "DELETE_USER"))
                .build();

        roleResponse = RoleResponse.builder()
                .name("ROLE_ADMIN")
                .description("Administrator role")
                .permissions(Set.of(new PermissionResponse("CREATE_USER"), new PermissionResponse("DELETE_USER")))
                .build();
    }

    @Test
    public void createRole_validRequest_success() throws Exception {
        // Given
        Mockito.when(roleService.create(ArgumentMatchers.any(RoleRequest.class))).thenReturn(roleResponse);

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.name").value("ROLE_ADMIN"));
    }

    @Test
    public void createRole_duplicateRoleName_fail() throws Exception {
        // Given
        Mockito.when(roleService.create(ArgumentMatchers.any(RoleRequest.class)))
                .thenThrow(new AppException(ErrorCode.UNAUTHENTICATED));

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Role already exists"));
    }

    @Test
    public void createRole_invalidRequest_fail() throws Exception {
        // Given
        roleRequest.setName(""); // Invalid name

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void getAllRoles_success() throws Exception {
        // Given
        List<RoleResponse> roles = List.of(
                RoleResponse.builder().name("ROLE_USER").description("User role").build(),
                RoleResponse.builder().name("ROLE_ADMIN").description("Admin role").build()
        );

        Mockito.when(roleService.getAll()).thenReturn(roles);

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.length()").value(2));
    }

    @Test
    public void deleteRole_validRole_success() throws Exception {
        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/roles/ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(roleService, Mockito.times(1)).delete("ROLE_USER");
    }

    @Test
    public void deleteRole_nonExistentRole_fail() throws Exception {
        // Given
        Mockito.doThrow(new AppException(ErrorCode.UNAUTHENTICATED)).when(roleService).delete("ROLE_NON_EXISTENT");

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/roles/ROLE_NON_EXISTENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Role not found"));
    }

    @Test
    public void deleteRole_invalidRole_fail() throws Exception {
        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/roles/INVALID_ROLE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void createRole_invalidPermission_fail() throws Exception {
        // Given
        roleRequest.setPermissions(Set.of("INVALID_PERMISSION"));

        Mockito.when(roleService.create(ArgumentMatchers.any(RoleRequest.class)))
                .thenThrow(new AppException(ErrorCode.UNAUTHENTICATED));

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid permission"));
    }

    @Test
    public void createRole_missingInformation_fail() throws Exception {
        // Given
        roleRequest.setDescription(null);
        roleRequest.setPermissions(null);

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void getAllRoles_noRolesExist_success() throws Exception {
        // Given
        Mockito.when(roleService.getAll()).thenReturn(Collections.emptyList());

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.length()").value(0));
    }
}
