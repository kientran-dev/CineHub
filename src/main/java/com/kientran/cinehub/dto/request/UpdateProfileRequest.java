package com.kientran.cinehub.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UpdateProfileRequest {

    @NotBlank(message = "First name should not be blank")
    String firstName;

    @NotBlank(message = "Last name should not be blank")
    String lastName;

    String avatarId;
}
