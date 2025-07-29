package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.ChangePasswordRequest;
import com.kientran.cinehub.dto.response.ChangePasswordResponse;
import com.kientran.cinehub.service.PasswordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PasswordController {

    PasswordService passwordService;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        String clientIp = getClientIpAddress(httpRequest);
        log.info("Password change request from user: {} with IP: {}", userEmail, clientIp);

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );

            log.warn("Validation failed for password change request from user: {}", userEmail);

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Validation failed",
                    "errors", errors
            ));
        }

        // Additional validation for password confirmation
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "New password and confirmation password do not match"
            ));
        }

        try {
            // Process password change
            ChangePasswordResponse response = passwordService.changePassword(request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            log.error("Unexpected error in password change controller for user: {}", userEmail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ChangePasswordResponse.failure("An unexpected error occurred"));
        } finally {
            // Always clear sensitive data
            request.clearPasswords();
        }
    }

    @PostMapping("/validate-current-password")
    public ResponseEntity<Map<String, Object>> validateCurrentPassword(
            @RequestBody Map<String, String> request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        String currentPassword = request.get("currentPassword");

        try {
            boolean isValid = passwordService.validateCurrentPassword(currentPassword);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("message", isValid ? "Password is valid" : "Password is invalid");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error validating current password for user: {}", userEmail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("valid", false, "message", "Validation error occurred"));
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ChangePasswordResponse> handleException(Exception e) {
        log.error("Unhandled exception in PasswordController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ChangePasswordResponse.failure("An unexpected error occurred"));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}