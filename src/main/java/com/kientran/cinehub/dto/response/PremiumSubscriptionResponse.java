package com.kientran.cinehub.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PremiumSubscriptionResponse {
    Long id;
    Long packageId;
    String packageName;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String status;
}