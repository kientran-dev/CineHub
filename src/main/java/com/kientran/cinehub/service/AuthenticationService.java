package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.AuthenticationRequest;
import com.kientran.cinehub.dto.request.RegisterRequest;
import com.kientran.cinehub.dto.response.AuthenticationResponse;
import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.repository.RefreshTokenRepository;
import com.kientran.cinehub.repository.RoleRepository;
import com.kientran.cinehub.repository.UserRepository;
import com.kientran.cinehub.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    public AuthenticationResponse register(RegisterRequest request) {
        // Kiểm tra trùng lặp để tránh lỗi Duplicate Key
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        var userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
        return createAuthResponse(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. Xác thực qua Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2. Lấy thông tin User
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 3. Tạo Response kèm lưu Token vào DB
        return createAuthResponse(user);
    }

    @Transactional
    public AuthenticationResponse refreshToken(String requestToken) {
        return refreshTokenRepository.findByToken(requestToken)
                .map(refreshTokenService::verifyExpiration)
                .map(refreshToken -> {
                    var user = refreshToken.getUser();
                    String accessToken = jwtService.generateToken(user);

                    // Trả về Access Token mới, giữ nguyên Refresh Token cũ (hoặc tạo mới nếu muốn xoay vòng)
                    return AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(requestToken)
                            .expiresIn(accessTokenExpiration)
                            .user(buildUserData(user))
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại trong hệ thống!"));
    }

    // Hàm helper tạo Response chung cho Login và Register
    private AuthenticationResponse createAuthResponse(User user) {
        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(accessTokenExpiration)
                .user(buildUserData(user))
                .build();
    }

    // Helper tạo thông tin user rút gọn cho Frontend
    private AuthenticationResponse.UserData buildUserData(User user) {
        return AuthenticationResponse.UserData.builder()
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roles(user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        // Tìm token trong DB, nếu thấy thì xóa
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}