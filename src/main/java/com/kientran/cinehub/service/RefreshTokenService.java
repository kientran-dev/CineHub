package com.kientran.cinehub.service;

import com.kientran.cinehub.entity.RefreshToken;
import com.kientran.cinehub.exception.RefreshTokenExpiredException;
import com.kientran.cinehub.repository.RefreshTokenRepositoty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepositoty refreshTokenRepositoty;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    public RefreshToken createRefreshToken(String userId, String tokenString) {
        // Kiểm tra xem đã tồn tại refresh token cho userId này chưa
        Optional<RefreshToken> existingToken = refreshTokenRepositoty.findByUserId(userId);

        RefreshToken refreshToken;
        if (existingToken.isPresent()) {
            refreshToken = existingToken.get();
            refreshToken.setToken(tokenString);
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        } else {
            refreshToken = new RefreshToken();
            refreshToken.setUserId(userId);
            refreshToken.setToken(tokenString);
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        }
        RefreshToken savedToken = refreshTokenRepositoty.save(refreshToken);
        return savedToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepositoty.delete(token);
            throw new RefreshTokenExpiredException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
    public void deleteByUserId(String userId) {
        refreshTokenRepositoty.deleteByUserId(userId);
    }
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepositoty.findByToken(token);
    }
}