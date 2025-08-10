package com.kientran.cinehub.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordResponse {

    boolean success;
    String message;

    // Static factory methods
    public static ChangePasswordResponse success() {
        return new ChangePasswordResponse(true, "Password changed successfully");
    }

    public static ChangePasswordResponse success(String message) {
        return new ChangePasswordResponse(true, message);
    }

    public static ChangePasswordResponse failure(String message) {
        return new ChangePasswordResponse(false, message);
    }
}
