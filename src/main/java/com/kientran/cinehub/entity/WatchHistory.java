package com.kientran.cinehub.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "watch_histories")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WatchHistory extends BaseEntity {

    @Field(name = "user_id")
    String userId;

    @Field(name = "movie_id")
    String movieId;

    @Field(name = "watched_duration_seconds")
    Long watchedDurationSeconds; // Thời gian đã xem (giây)

    @Field(name = "total_duration_seconds")
    Long totalDurationSeconds; // Tổng thời lượng phim

    @Field(name = "watch_percentage")
    Double watchPercentage; // % đã xem

    @Field(name = "last_watched_at")
    LocalDateTime lastWatchedAt; // Lần cuối xem

    @Field(name = "is_completed")
    Boolean isCompleted = false; // Check xem đã xem ht hay chưa
}