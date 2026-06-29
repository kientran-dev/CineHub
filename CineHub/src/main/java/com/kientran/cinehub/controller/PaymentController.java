package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.PaymentRequest;
import com.kientran.cinehub.dto.response.PaymentResponse;
import com.kientran.cinehub.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;

    @Value("${vnpay.frontend-redirect-url}")
    @NonFinal
    String frontendRedirectUrl;

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(paymentService.getMyPayments(username));
    }

    @PostMapping("/create-payment")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request,
            Authentication authentication,
            HttpServletRequest servletRequest) {
        String username = authentication.getName();
        return new ResponseEntity<>(paymentService.createPayment(request, username, servletRequest), HttpStatus.CREATED);
    }

    @GetMapping("/vnpay-return")
    public void vnpayCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PaymentResponse result = paymentService.processPaymentCallback(request);
        if ("SUCCESS".equals(result.getStatus())) {
            // Thanh toán thành công, chuyển hướng về trang profile hoặc trang thông báo
            response.sendRedirect(frontendRedirectUrl + "?payment=success");
        } else {
            response.sendRedirect(frontendRedirectUrl + "?payment=failed");
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(id, status));
    }
}