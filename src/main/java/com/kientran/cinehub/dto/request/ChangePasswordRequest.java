package com.kientran.cinehub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password should not be blank")
    @Size(min = 6, max = 128, message = "Password should be between 6 and 128 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one number"
    )
    String newPassword;

    @NotBlank(message = "Password confirmation is required")
    private String confirmNewPassword;

    // Security method to clear sensitive data
    public void clearPasswords() {
        this.currentPassword = null;
        this.newPassword = null;
        this.confirmNewPassword = null;
    }

    @Override
    public String toString() {
        return "ChangePasswordRequest{" +
                "currentPassword='[PROTECTED]'" +
                ", newPassword='[PROTECTED]'" +
                ", confirmNewPassword='[PROTECTED]'" +
                '}';
    }
}