package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.PaymentRequest;
import com.kientran.cinehub.dto.response.PaymentResponse;
import com.kientran.cinehub.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request,
            Authentication authentication,
            HttpServletRequest servletRequest) {
        String username = authentication.getName();
        return new ResponseEntity<>(paymentService.createPayment(request, username, servletRequest), HttpStatus.CREATED);
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<PaymentResponse> vnpayCallback(HttpServletRequest request) {
        // This endpoint will be called by VNPay after user payment
        return ResponseEntity.ok(paymentService.processPaymentCallback(request));
    }
}