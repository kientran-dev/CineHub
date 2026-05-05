package com.kientran.cinehub.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PremiumPackageRequest {
    String packageName;
    BigDecimal price;
    Integer durationDays;
    String description;
    Integer rewardPoints; // Điểm tích lũy tặng khi mua gói này
}
