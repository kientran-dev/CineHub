package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.response.DashboardResponse;
import com.kientran.cinehub.entity.Payment;
import com.kientran.cinehub.repository.MovieRepository;
import com.kientran.cinehub.repository.PaymentRepository;
import com.kientran.cinehub.repository.PremiumSubscriptionRepository;
import com.kientran.cinehub.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardService {
    UserRepository userRepository;
    MovieRepository movieRepository;
    PaymentRepository paymentRepository;
    PremiumSubscriptionRepository subscriptionRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalMovies = movieRepository.count();

        long premiumUsers = subscriptionRepository.findAll().stream()
                .filter(sub -> "ACTIVE".equals(sub.getStatus()) && sub.getEndDate() != null && sub.getEndDate().isAfter(LocalDateTime.now()))
                .map(sub -> sub.getUser().getId())
                .distinct()
                .count();

        BigDecimal totalRevenue = paymentRepository.findAll().stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()) && p.getAmount() != null)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalMovies(totalMovies)
                .premiumUsers(premiumUsers)
                .totalRevenue(totalRevenue)
                .build();
    }
}
