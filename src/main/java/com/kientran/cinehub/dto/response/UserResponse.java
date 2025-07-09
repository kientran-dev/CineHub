package com.kientran.cinehub.dto.response;

import com.kientran.cinehub.enums.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;

    String email;

    String firstName;

    String lastName;

    Set<UserRole> roles;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
