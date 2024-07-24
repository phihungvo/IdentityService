package com.luv2code.IdentityService.dto.request;

import com.luv2code.IdentityService.entity.Role;
import com.luv2code.IdentityService.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3, message = "USERNAME_INVALID")
    String username;
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
    String firstname;
    String lastname;

    @DobConstraint(min = 2, message = "INVALID_DOB")
    LocalDate dob;

}
