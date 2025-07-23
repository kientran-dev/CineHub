package com.kientran.cinehub.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "episodes")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Episode extends BaseEntity {

    @Field(name = "content_id")
    String contentId; // ID của series

    @Field(name = "season_id")
    String seasonId; // ID của season

    @Field(name = "episode_number")
    Integer episodeNumber; // Tập thứ mấy

    @Field(name = "name")
    String name; // Tên tập

    @Field(name = "still_path")
    String stillPath; // Ảnh thumbnail của tập

    @Field(name = "duration_seconds")
    Long durationSeconds; // Thời lượng tập (giây)

    @Field(name = "video_sources")
    List<VideoSource> videoSources; // Danh sách video với chất lượng khác nhau

    @Field(name = "is_deleted")
    Boolean isDeleted = false;
}
