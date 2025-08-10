package com.kientran.cinehub.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WatchProgressResponse {
    String id;
    String userId;
    String contentId;
    String seasonId;
    String episodeId;
    Integer seasonNumber;
    Integer episodeNumber;
    String contentTitle;
    String contentPosterPath;
    String episodeName;
    Long currentTimeSeconds;
    Long totalDurationSeconds;
    Double progressPercentage;
    Boolean isCompleted;
    LocalDateTime lastWatchedAt;
}