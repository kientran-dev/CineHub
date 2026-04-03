package com.kientran.cinehub.service;

import com.kientran.cinehub.entity.RefreshToken;
import com.kientran.cinehub.repository.RefreshTokenRepository;
import com.kientran.cinehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RefreshTokenService {

    @Value("${jwt.refresh-token-expiration}")
    long refreshExpiration; // Lấy từ application.properties

    final RefreshTokenRepository refreshTokenRepository;
    final UserRepository userRepository;

    @Transactional
    public RefreshToken createRefreshToken(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Xóa token cũ của user nếu có (để mỗi user chỉ có 1 session refresh)
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString()) // Dùng chuỗi ngẫu nhiên cho bảo mật
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

}