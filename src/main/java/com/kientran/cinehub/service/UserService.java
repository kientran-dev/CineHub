package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.UserUpdateRequest;
import com.kientran.cinehub.dto.response.UserResponse;
import com.kientran.cinehub.entity.PremiumSubscription;
import com.kientran.cinehub.entity.Role;
import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.repository.RoleRepository;
import com.kientran.cinehub.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;

    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse updateProfile(String username, UserUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Chỉ cập nhật field nào được gửi (partial update)
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        user = userRepository.save(user);
        return mapToResponse(user);
    }

    @Transactional
    public void grantAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        boolean hasAdmin = user.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
        if (!hasAdmin) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            user.getRoles().add(adminRole);
            userRepository.save(user);
        }
    }

    @Transactional
    public void revokeAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.getRoles().removeIf(r -> "ROLE_ADMIN".equals(r.getName()));
        userRepository.save(user);
    }

    @Transactional
    public UserResponse claimBirthdayReward(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getDateOfBirth() == null) {
            throw new RuntimeException("Bạn chưa cập nhật ngày sinh trong hồ sơ.");
        }

        LocalDate today = LocalDate.now();
        LocalDate dob = user.getDateOfBirth();

        // Kiểm tra hôm nay có phải sinh nhật không (so sánh ngày + tháng)
        if (today.getMonthValue() != dob.getMonthValue() || today.getDayOfMonth() != dob.getDayOfMonth()) {
            throw new RuntimeException("Hôm nay không phải sinh nhật của bạn.");
        }

        // Kiểm tra đã nhận trong năm nay chưa (dựa vào lastBirthdayRewardYear)
        if (user.getLastBirthdayRewardYear() != null && user.getLastBirthdayRewardYear() == today.getYear()) {
            throw new RuntimeException("Bạn đã nhận điểm sinh nhật trong năm nay rồi.");
        }

        // Tặng 20 điểm
        int currentPoints = user.getRewardPoints() != null ? user.getRewardPoints() : 0;
        user.setRewardPoints(currentPoints + 20);
        user.setLastBirthdayRewardYear(today.getYear());
        user = userRepository.save(user);

        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        // Tìm subscription ACTIVE còn hạn
        PremiumSubscription activeSub = user.getSubscription();
        boolean isPremium = activeSub != null
                && "ACTIVE".equals(activeSub.getStatus())
                && activeSub.getEndDate() != null
                && activeSub.getEndDate().isAfter(LocalDateTime.now());
                 
        List<String> roles = user.getRoles().stream()
                 .map(Role::getName)
                 .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatar(user.getAvatar())
                .dateOfBirth(user.getDateOfBirth())
                .rewardPoints(user.getRewardPoints())
                .lastBirthdayRewardYear(user.getLastBirthdayRewardYear())
                .roles(roles)
                .isPremium(isPremium)
                .premiumPackageName(null)
                .premiumEndDate(isPremium ? activeSub.getEndDate() : null)
                .registeredDate(user.getCreatedAt())
                .build();
    }
}