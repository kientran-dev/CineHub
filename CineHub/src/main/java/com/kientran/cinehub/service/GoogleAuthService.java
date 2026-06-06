package com.kientran.cinehub.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.kientran.cinehub.dto.response.AuthenticationResponse;
import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.repository.RoleRepository;
import com.kientran.cinehub.repository.UserRepository;
import com.kientran.cinehub.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Transactional
    public AuthenticationResponse loginWithGoogle(String idToken) {
        try {
            // 1. Xác minh idToken với Google
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new RuntimeException("Invalid Google token");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            // 2. Tìm user theo email hoặc tạo mới
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                // Tạo username từ phần đầu email (tránh trùng lặp)
                String baseUsername = email.split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
                String username = baseUsername;
                int suffix = 1;
                while (userRepository.existsByUsername(username)) {
                    username = baseUsername + suffix++;
                }

                var userRole = roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("Role not found"));

                User newUser = User.builder()
                        .username(username)
                        .password(UUID.randomUUID().toString()) // Mật khẩu ngẫu nhiên (user không dùng)
                        .email(email)
                        .fullName(name != null ? name : username)
                        .avatar(picture)
                        .roles(Set.of(userRole))
                        .build();

                return userRepository.save(newUser);
            });

            // 3. Cập nhật avatar nếu chưa có (user đăng nhập Google lần đầu)
            if (user.getAvatar() == null && picture != null) {
                user.setAvatar(picture);
                userRepository.save(user);
            }

            // 4. Tạo JWT tokens
            String accessToken = jwtService.generateToken(user);
            var refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

            AuthenticationResponse.UserData userData = AuthenticationResponse.UserData.builder()
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .roles(user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet()))
                    .build();

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .expiresIn(accessTokenExpiration)
                    .user(userData)
                    .build();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Google authentication failed: " + e.getMessage());
        }
    }
}
