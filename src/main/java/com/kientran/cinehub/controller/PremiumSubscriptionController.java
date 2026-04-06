package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.PremiumSubscriptionRequest;
import com.kientran.cinehub.dto.response.PremiumSubscriptionResponse;
import com.kientran.cinehub.service.PremiumSubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public ResponseEntity<List<PremiumSubscriptionResponse>> getMySubscriptions(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(subscriptionService.getMySubscriptions(username));
    }
}