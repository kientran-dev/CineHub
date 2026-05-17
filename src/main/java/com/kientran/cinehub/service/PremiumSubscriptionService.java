package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.response.PremiumSubscriptionResponse;
import com.kientran.cinehub.entity.Payment;
import com.kientran.cinehub.entity.PremiumPackage;
import com.kientran.cinehub.entity.PremiumSubscription;
import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.repository.PremiumSubscriptionRepository;
import com.kientran.cinehub.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PremiumSubscriptionService {

    PremiumSubscriptionRepository subscriptionRepository;
    UserRepository userRepository;

    @Transactional
    public void activatePremium(Payment payment) {
        User user = payment.getUser();
        PremiumPackage premiumPackage = payment.getPremiumPackage();

        // Tìm subscription hiện có của user (chỉ có tối đa 1)
        PremiumSubscription subscription = subscriptionRepository.findByUserId(user.getId())
                .orElse(null);

        LocalDateTime now = LocalDateTime.now();

        if (subscription == null) {
            // Chưa có → tạo mới
            subscription = PremiumSubscription.builder()
                    .user(user)
                    .startDate(now)
                    .endDate(now.plusDays(premiumPackage.getDurationDays()))
                    .status("ACTIVE")
                    .build();
        } else {
            // Đã có → cộng dồn endDate
            // Nếu đang còn hạn: endDate = endDate cũ + durationDays
            // Nếu đã hết hạn: endDate = now + durationDays
            LocalDateTime baseDate = subscription.getEndDate() != null && subscription.getEndDate().isAfter(now)
                    ? subscription.getEndDate()
                    : now;
            subscription.setEndDate(baseDate.plusDays(premiumPackage.getDurationDays()));
            subscription.setStatus("ACTIVE");
        }

        subscription = subscriptionRepository.save(subscription);

        // Gắn payment vào subscription
        payment.setPremiumSubscription(subscription);

        // Tặng điểm tích lũy cho user
        int pointsToAward = (premiumPackage.getRewardPoints() != null) ? premiumPackage.getRewardPoints() : 0;
        if (pointsToAward > 0) {
            int currentPoints = user.getRewardPoints() != null ? user.getRewardPoints() : 0;
            user.setRewardPoints(currentPoints + pointsToAward);
            userRepository.save(user);
        }
    }

    public PremiumSubscriptionResponse getActiveSubscription(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.findByUserId(user.getId())
                .filter(sub -> "ACTIVE".equals(sub.getStatus()) && sub.getEndDate() != null && sub.getEndDate().isAfter(LocalDateTime.now()))
                .map(this::mapToResponse)
                .orElse(null);
    }

    private PremiumSubscriptionResponse mapToResponse(PremiumSubscription subscription) {
        return PremiumSubscriptionResponse.builder()
                .id(subscription.getId())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .build();
    }
}