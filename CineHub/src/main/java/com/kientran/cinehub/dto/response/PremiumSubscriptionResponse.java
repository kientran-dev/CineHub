package com.kientran.cinehub.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PremiumSubscriptionResponse {
    Long id;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String status;
}