package com.kientran.cinehub.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "watch_progress")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WatchProgress extends BaseEntity {

    @Field(name = "user_id")
    String userId;

    @Field(name = "content_id")
    String contentId;

    // =============== EPISODE SPECIFIC (nullable for movies) ===============
    @Field(name = "season_id")
    String seasonId; // null nếu là phim lẻ

    @Field(name = "episode_id")
    String episodeId; // null nếu là phim lẻ

    @Field(name = "season_number")
    Integer seasonNumber; // null nếu là phim lẻ

    @Field(name = "episode_number")
    Integer episodeNumber; // null nếu là phim lẻ

    // =============== PROGRESS TRACKING ===============
    @Field(name = "current_time_seconds")
    Long currentTimeSeconds; // Giây hiện tại đang xem

    @Field(name = "total_duration_seconds")
    Long totalDurationSeconds; // Tổng thời lượng video

    @Field(name = "progress_percentage")
    Double progressPercentage; // % đã xem (0.0 - 100.0)

    @Field(name = "is_completed")
    Boolean isCompleted = false; // Đã xem hết chưa (>= 90%)

    @Field(name = "last_watched_at")
    LocalDateTime lastWatchedAt; // Lần cuối xem

}