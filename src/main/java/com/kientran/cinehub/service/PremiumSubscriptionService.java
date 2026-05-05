package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.PremiumSubscriptionRequest;
import com.kientran.cinehub.dto.response.PremiumSubscriptionResponse;
import com.kientran.cinehub.entity.Payment;
import com.kientran.cinehub.entity.PremiumPackage;
import com.kientran.cinehub.entity.PremiumSubscription;
import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.repository.PaymentRepository;
import com.kientran.cinehub.repository.PremiumPackageRepository;
import com.kientran.cinehub.repository.PremiumSubscriptionRepository;
import com.kientran.cinehub.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PremiumSubscriptionService {

    PremiumSubscriptionRepository subscriptionRepository;
    PremiumPackageRepository packageRepository;
    UserRepository userRepository;
    PaymentRepository paymentRepository;

    @Transactional
    public void activatePremium(Payment payment) {
        User user = payment.getUser();
        PremiumPackage premiumPackage = payment.getPremiumPackage();

        // 1. Chống lỗi duplicate key nếu thao tác DB thủ công:
        // Một Payment chỉ được tạo duy nhất 1 Subscription (OneToOne).
        if (subscriptionRepository.existsByPaymentId(payment.getId())) {
            return; // Đã từng tạo subscription cho payment này rồi, bỏ qua
        }

        // 2. Kiểm tra xem user có gói nào đang active không
        PremiumSubscription activeSub = subscriptionRepository.findByUserId(user.getId()).stream()
                .filter(sub -> "ACTIVE".equals(sub.getStatus()) && sub.getEndDate() != null && sub.getEndDate().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate;

        // Nếu user đang có gói active, cộng dồn ngày vào endDate của gói cũ
        if (activeSub != null) {
            endDate = activeSub.getEndDate().plusDays(premiumPackage.getDurationDays());
            // Cập nhật luôn gói cũ thành INACTIVE để tạo gói mới với lịch sử đầy đủ
            activeSub.setStatus("INACTIVE");
            subscriptionRepository.save(activeSub);
        } else {
            endDate = startDate.plusDays(premiumPackage.getDurationDays());
        }

        PremiumSubscription subscription = PremiumSubscription.builder()
                .user(user)
                .payment(payment)
                .startDate(startDate)
                .endDate(endDate)
                .status("ACTIVE")
                .build();

        subscriptionRepository.save(subscription);

        // Cập nhật ngược lại ID subscription cho payment để đối soát
        payment.setPremiumSubscription(subscription);

        // Tặng điểm tích lũy cho user dựa trên gói đã mua
        int pointsToAward = (premiumPackage.getRewardPoints() != null) ? premiumPackage.getRewardPoints() : 0;
        if (pointsToAward > 0) {
            int currentPoints = user.getRewardPoints() != null ? user.getRewardPoints() : 0;
            user.setRewardPoints(currentPoints + pointsToAward);
            userRepository.save(user);
        }
    }

    public List<PremiumSubscriptionResponse> getMySubscriptions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PremiumSubscriptionResponse getActiveSubscription(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.findByUserId(user.getId()).stream()
                .filter(sub -> "ACTIVE".equals(sub.getStatus()) && sub.getEndDate() != null && sub.getEndDate().isAfter(LocalDateTime.now()))
                .findFirst()
                .map(this::mapToResponse)
                .orElse(null);
    }

    private PremiumSubscriptionResponse mapToResponse(PremiumSubscription subscription) {
        return PremiumSubscriptionResponse.builder()
                .id(subscription.getId())
                .packageId(subscription.getPayment().getPremiumPackage().getId())
                .packageName(subscription.getPayment().getPremiumPackage().getPackageName())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .build();
    }
}