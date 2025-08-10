package com.kientran.cinehub.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FavoriteResponse {
    String id;
    String userId;
    String contentId;
    String contentTitle;
    String contentPosterPath;
    String contentOverview;
    Double averageRating;
    Integer ratingCount;
    LocalDateTime addedAt;
}