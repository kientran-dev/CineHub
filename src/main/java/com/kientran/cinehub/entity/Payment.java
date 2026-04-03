package com.kientran.cinehub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Payment extends BaseEntity{
    @Column(name = "payment_date")
    LocalDateTime paymentDate;

    @Column(name = "payment_method")
    String paymentMethod;

    @Column(name = "amount")
    BigDecimal amount;

    @Column(name = "status")
    String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "premium_package_id")
    PremiumPackage premiumPackage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "premium_subscription_id")
    PremiumSubscription premiumSubscription;
}