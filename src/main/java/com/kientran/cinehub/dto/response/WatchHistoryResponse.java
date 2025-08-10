package com.kientran.cinehub.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WatchHistoryResponse {
    String id;
    String userId;
    String movieId;
    String contentTitle;
    String contentPosterPath;
    Long watchedDurationSeconds;
    Long totalDurationSeconds;
    Double watchPercentage;
    Boolean isCompleted;
    LocalDateTime lastWatchedAt;
}
