package com.kientran.cinehub.security;

import com.kientran.cinehub.dto.response.UserResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    // Lấy khóa bí mật từ chuỗi base64
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Trích xuất tất cả các claims từ token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Trích xuất một claim cụ thể từ token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Trích xuất email (subject) từ token
    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Trích xuất ngày hết hạn từ token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Kiểm tra xem token đã hết hạn chưa
    protected Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Tạo access token
    public String generateAccessToken(UserResponse userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Bạn có thể thêm các claims khác vào đây nếu cần, ví dụ: vai trò, id người dùng
        claims.put("id", userDetails.getId());
        claims.put("roles", userDetails.getRoles());

        return generateToken(claims, userDetails.getEmail(), accessTokenExpirationMs);
    }

    // Tạo refresh token
    public String generateRefreshToken(UserResponse userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDetails.getId());
        return generateToken(claims, userDetails.getEmail(), refreshTokenExpirationMs);
    }

    // Phương thức chung để tạo token
    private String generateToken(Map<String, Object> extraClaims, String subject, long expirationTime) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    // Xác thực token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String userEmail = extractUserEmail(token);
        return (userEmail.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}