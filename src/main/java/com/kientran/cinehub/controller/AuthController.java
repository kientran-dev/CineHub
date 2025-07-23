package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.LoginRequest;
import com.kientran.cinehub.dto.request.UserRegistrationRequest;
import com.kientran.cinehub.dto.response.AuthResponse;
import com.kientran.cinehub.dto.response.UserResponse;
import com.kientran.cinehub.security.JwtService;
import com.kientran.cinehub.service.RefreshTokenService;
import com.kientran.cinehub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse response = userService.registerNewUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserResponse userResponse = userService.getUserResponseByEmail(userDetails.getUsername());

        String accessToken = jwtService.generateAccessToken(userResponse);
        String refreshToken = jwtService.generateRefreshToken(userResponse);
        refreshTokenService.createRefreshToken(userResponse.getId(), refreshToken);
        AuthResponse authResponse = new AuthResponse(accessToken, refreshToken, "Bearer", userResponse.getEmail());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody String refreshToken) {
        log.info("Received refresh token request.");
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(token -> {
                    UserResponse userResponse = userService.getUserResponseById(token.getUserId());
                    String newAccessToken = jwtService.generateAccessToken(userResponse);
                    return new AuthResponse(newAccessToken, refreshToken, "Bearer", userResponse.getEmail());
                })
                .map(authResponse -> new ResponseEntity<>(authResponse, HttpStatus.OK))
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            // Lấy token từ header Authorization
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Token không hợp lệ");
            }
            final String jwt = authHeader.substring(7);
            String userEmail = jwtService.extractUserEmail(jwt);

            if (userEmail != null) {
                UserResponse userResponse = userService.getUserResponseByEmail(userEmail);
                refreshTokenService.deleteByUserId(userResponse.getId());
                return ResponseEntity.ok("Logout thành công");
            } else {
                return ResponseEntity.badRequest().body("Token không hợp lệ");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi logout");
        }
    }
}