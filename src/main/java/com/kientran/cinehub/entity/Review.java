package com.kientran.cinehub.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "reviews")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review extends BaseEntity {

    @Field(name = "user_id")
    String userId;

    @Field(name = "content_id")
    String contentId;

    @Field(name = "season_id")
    String seasonId; // ID của season nếu là series, null nếu là movie

    @Field(name = "comment")
    String comment; // Nội dung đánh giá

    @Field(name = "total_votes")
    Integer totalVotes = 0; // Tổng số vote

    @Field(name = "moderated_by")
    String moderatedBy; // Admin/Moderator ID

    @Field(name = "comment_at")
    LocalDateTime commentAt; // Thời gian bình luận

    @Field(name = "reported_count")
    Integer reportedCount = 0; // Số lần bị báo cáo


}