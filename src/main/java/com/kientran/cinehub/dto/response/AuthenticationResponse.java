package com.kientran.cinehub.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String accessToken;
    String refreshToken;
    long expiresIn;
    UserData user;

    @Data
    @Builder
    @JsonPropertyOrder({"username", "email", "fullName", "roles"})
    @FieldDefaults(level = lombok.AccessLevel.PRIVATE)
    public static class UserData {
        String username;
        String fullName;
        String email;
        Set<String> roles;
    }
}