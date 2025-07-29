package com.kientran.cinehub.dto.response;

import com.kientran.cinehub.enums.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;

    String email;

    String firstName;

    String lastName;

    String avatarId;

    String avatarUrl;

    Set<UserRole> roles;

}
