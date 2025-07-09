package com.kientran.cinehub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng BCryptPasswordEncoder để mã hóa mật khẩu
        return new BCryptPasswordEncoder();
    }
    // 2. Định nghĩa SecurityFilterChain
    // Đây là nơi bạn cấu hình quy tắc bảo mật cho các HTTP request
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF (Cross-Site Request Forgery) protection
                // Thường tắt cho các API RESTful vì chúng ta không dùng session-based authentication
                // Trong ứng dụng web truyền thống, bạn nên bật nó.
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép endpoint đăng ký công khai (public)
                        // Bất kỳ request nào đến /api/auth/register đều không cần xác thực
                        .requestMatchers("/api/auth/register").permitAll()
                        // Cho phép truy cập công khai vào Swagger UI và các tài liệu API (nếu có)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Bất kỳ request nào khác đều yêu cầu xác thực
                        .anyRequest().authenticated()
                );
        // .httpBasic(withDefaults()); // Hoặc .formLogin(withDefaults()); nếu bạn muốn dùng cơ chế đăng nhập mặc định của Spring Security

        return http.build();
    }

    // Bạn sẽ cần định nghĩa UserDetailsService sau này để xử lý logic đăng nhập
    // (Hiện tại chưa cần ngay để khắc phục lỗi 401 cho register)
    // Ví dụ:
    /*
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> userRepository.findByEmail(email)
            .map(user -> new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                    .collect(Collectors.toList())
            ))
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    */
}
