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

        LocalDateTime startDate = LocalDateTime.now();
        // Nếu user đang có gói active, cộng dồn ngày vào endDate của gói cũ
        // Ở đây làm đơn giản là tạo mới từ thời điểm hiện tại
        LocalDateTime endDate = startDate.plusDays(premiumPackage.getDurationDays());

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
    }

    public List<PremiumSubscriptionResponse> getMySubscriptions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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