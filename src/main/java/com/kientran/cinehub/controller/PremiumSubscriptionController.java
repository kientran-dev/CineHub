package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.response.PremiumSubscriptionResponse;
import com.kientran.cinehub.service.PremiumSubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PremiumSubscriptionController {

    PremiumSubscriptionService subscriptionService;

    @GetMapping("/my")
    public ResponseEntity<List<PremiumSubscriptionResponse>> getMySubscriptions(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(subscriptionService.getMySubscriptions(username));
    }

    @GetMapping("/my/active")
    public ResponseEntity<PremiumSubscriptionResponse> getMyActiveSubscription(Authentication authentication) {
        String username = authentication.getName();
        PremiumSubscriptionResponse active = subscriptionService.getActiveSubscription(username);
        if (active == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(active);
    }
}