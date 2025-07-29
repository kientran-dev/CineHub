package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.ChangePasswordRequest;
import com.kientran.cinehub.dto.response.ChangePasswordResponse;
import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.exception.UserNotFoundException;
import com.kientran.cinehub.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Slf4j
public class PasswordService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        try {
            // Validate password confirmation match
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                return ChangePasswordResponse.failure("New password and confirmation password do not match");
            }

            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Find user by email
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

            // Verify current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                log.warn("Invalid current password attempt for user: {}", userEmail);
                return ChangePasswordResponse.failure("Current password is incorrect");
            }

            // Check if new password is same as current
            if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                log.warn("New password same as current for user: {}", userEmail);
                return ChangePasswordResponse.failure("New password must be different from current password");
            }

            // Encode new password
            String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

            // Update password
            user.setPassword(encodedNewPassword);

            // Save user
            userRepository.save(user);

            // Clear sensitive data from request
            request.clearPasswords();

            log.info("Password changed successfully for user: {}", userEmail);

            return ChangePasswordResponse.success("Password changed successfully");

        } catch (UserNotFoundException e) {
            log.warn("Password change failed - user not found: {}", e.getMessage());
            return ChangePasswordResponse.failure("User not found");

        } catch (Exception e) {
            log.error("Unexpected error during password change", e);
            return ChangePasswordResponse.failure("An error occurred while changing password. Please try again.");
        }
    }

    public boolean validateCurrentPassword(String currentPassword) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            return passwordEncoder.matches(currentPassword, user.getPassword());

        } catch (Exception e) {
            log.error("Error validating current password", e);
            return false;
        }
    }
}
