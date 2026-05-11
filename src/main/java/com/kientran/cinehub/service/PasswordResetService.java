package com.kientran.cinehub.service;

import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Lưu OTP trong memory (key: email, value: {otp, expiry})
    // Trong production nên dùng Redis; đây dùng ConcurrentHashMap cho demo
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    private record OtpEntry(String otp, LocalDateTime expiry) {}

    /**
     * Gửi OTP về email để đặt lại mật khẩu
     */
    @Transactional(readOnly = true)
    public void sendForgotPasswordOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này."));

        // Sinh OTP 6 số
        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        otpStore.put(email, new OtpEntry(otp, expiry));

        emailService.sendPasswordResetOtp(email,
                user.getFullName() != null ? user.getFullName() : user.getUsername(),
                otp);
    }

    /**
     * Xác minh OTP và đặt mật khẩu mới
     */
    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        OtpEntry entry = otpStore.get(email);
        if (entry == null) {
            throw new RuntimeException("Mã OTP không hợp lệ hoặc đã hết hạn.");
        }
        if (LocalDateTime.now().isAfter(entry.expiry())) {
            otpStore.remove(email);
            throw new RuntimeException("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
        }
        if (!entry.otp().equals(otp)) {
            throw new RuntimeException("Mã OTP không đúng.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        otpStore.remove(email); // Xóa OTP sau khi dùng
    }
}
