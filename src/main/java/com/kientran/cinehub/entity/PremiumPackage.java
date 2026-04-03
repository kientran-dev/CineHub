package com.kientran.cinehub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "premium_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PremiumPackage extends BaseEntity{
    @Column(name = "package_name")
    String packageName;

    @Column(name = "price")
    BigDecimal price;

    @Column(name = "duration_days")
    Integer durationDays;

    @OneToMany(mappedBy = "premiumPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Payment> payments;
}