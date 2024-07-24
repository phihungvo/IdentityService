package com.luv2code.IdentityService.dto.response;

import com.luv2code.IdentityService.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
//  String password;
    String firstname;
    String lastname;
    LocalDate dob;
    Set<RoleResponse> roles;
}
