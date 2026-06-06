package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.*;
import com.kientran.cinehub.dto.response.AuthenticationResponse;
import com.kientran.cinehub.service.AuthenticationService;
import com.kientran.cinehub.service.GoogleAuthService;
import com.kientran.cinehub.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService service;
    GoogleAuthService googleAuthService;
    PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthenticationResponse> googleLogin(
            @RequestBody GoogleAuthRequest request
    ) {
        return ResponseEntity.ok(googleAuthService.loginWithGoogle(request.getIdToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(service.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request) {
        service.logout(request.getRefreshToken());
        return ResponseEntity.ok("Đăng xuất thành công!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendForgotPasswordOtp(request.getEmail());
        return ResponseEntity.ok("Mã OTP đã được gửi về email của bạn.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công!");
    }
}