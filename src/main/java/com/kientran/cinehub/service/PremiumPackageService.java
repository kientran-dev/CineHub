package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.PremiumPackageRequest;
import com.kientran.cinehub.dto.response.PremiumPackageResponse;
import com.kientran.cinehub.entity.Payment;
import com.kientran.cinehub.entity.PremiumPackage;
import com.kientran.cinehub.repository.PremiumPackageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PremiumPackageService {

    PremiumPackageRepository premiumPackageRepository;

    @Transactional(readOnly = true)
    public List<PremiumPackageResponse> getAllPackages() {
        return premiumPackageRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PremiumPackageResponse getPackageById(Long id) {
        PremiumPackage pkg = premiumPackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        return mapToResponse(pkg);
    }

    @Transactional
    public PremiumPackageResponse createPackage(PremiumPackageRequest request) {
        PremiumPackage pkg = PremiumPackage.builder()
                .packageName(request.getPackageName())
                .price(request.getPrice())
                .durationDays(request.getDurationDays())
                .description(request.getDescription())
                .build();
        return mapToResponse(premiumPackageRepository.save(pkg));
    }

    @Transactional
    public PremiumPackageResponse updatePackage(Long id, PremiumPackageRequest request) {
        PremiumPackage pkg = premiumPackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        pkg.setPackageName(request.getPackageName());
        pkg.setPrice(request.getPrice());
        pkg.setDurationDays(request.getDurationDays());
        pkg.setDescription(request.getDescription());
        return mapToResponse(premiumPackageRepository.save(pkg));
    }

    @Transactional
    public void deletePackage(Long id) {
        if (!premiumPackageRepository.existsById(id)) {
            throw new RuntimeException("Package not found");
        }
        premiumPackageRepository.deleteById(id);
    }

    private PremiumPackageResponse mapToResponse(PremiumPackage pkg) {
        long activeUsers = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        
        if (pkg.getPayments() != null) {
            totalRevenue = pkg.getPayments().stream()
                    .filter(p -> "SUCCESS".equals(p.getStatus()) && p.getAmount() != null)
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            activeUsers = pkg.getPayments().stream()
                    .filter(p -> "SUCCESS".equals(p.getStatus()) && p.getPremiumSubscription() != null 
                            && "ACTIVE".equals(p.getPremiumSubscription().getStatus())
                            && p.getPremiumSubscription().getEndDate() != null
                            && p.getPremiumSubscription().getEndDate().isAfter(LocalDateTime.now()))
                    .map(p -> p.getUser().getId())
                    .distinct()
                    .count();
        }

        return PremiumPackageResponse.builder()
                .id(pkg.getId())
                .packageName(pkg.getPackageName())
                .price(pkg.getPrice())
                .durationDays(pkg.getDurationDays())
                .description(pkg.getDescription())
                .activeUsers(activeUsers)
                .totalRevenue(totalRevenue)
                .build();
    }
}
