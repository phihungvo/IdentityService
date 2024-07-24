package com.luv2code.IdentityService.dto.request;

import com.luv2code.IdentityService.validator.DobConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String password;
    String firstname;
    String lastname;

    @DobConstraint(min = 2, message = "INVALID_DOB")
    LocalDate dob;

    List<String> roles;
}
