package com.luv2code.IdentityService.dto.request;

import com.luv2code.IdentityService.entity.Permission;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleRequest {
    String name;
    String description;
    Set<String> permissions;
}
