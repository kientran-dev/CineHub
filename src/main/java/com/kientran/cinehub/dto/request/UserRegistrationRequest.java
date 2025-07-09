package com.kientran.cinehub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRegistrationRequest {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email should not be blank")
    String email;

    @NotBlank(message = "Password should not be blank")
    @Size(min = 6, message = "Password should be at least 6 characters long")
    String password;

    @NotBlank(message = "First name should not be blank")
    String firstName;

    @NotBlank(message = "Last name should not be blank")
    String lastName;
}
