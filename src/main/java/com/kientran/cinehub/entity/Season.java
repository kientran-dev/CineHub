package com.kientran.cinehub.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Document(collection = "seasons")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Season extends BaseEntity {

    @Field(name = "content_id")
    String contentId; // ID của series

    @Field(name = "season_number")
    Integer seasonNumber;

    @Field(name = "name")
    String name;

    @Field(name = "overview")
    String overview;

    @Field(name = "poster_path")
    String posterPath;

    @Field(name = "episode_count")
    Integer episodeCount; // Số tập trong mùa này

    @Field(name = "is_deleted")
    Boolean isDeleted = false;
}