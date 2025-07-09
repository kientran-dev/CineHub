package com.kientran.cinehub.entity;

import com.kientran.cinehub.enums.MovieStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Document(collection = "movies")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Movie extends BaseEntity {

    @Field(name = "title")
    String title;

    @Field(name = "overview")
    String overview;

    @Field(name = "release_date")
    java.time.LocalDate releaseDate;

    @Field(name = "poster_path")
    String posterPath;

    @Field(name = "backdrop_path")
    String backdropPath;

    @Field(name = "vote_average")
    Double voteAverage;

    @Field(name = "vote_count")
    Long voteCount;

    @Field(name = "popularity")
    Double popularity;

    @Field(name = "genres")
    Set<Genre> genres;

    @Field(name = "runtime")
    Integer runtime;

    @Field(name = "tagline")
    String tagline;

    @Field(name = "status")
    MovieStatus status;

    @Field(name = "imdbId") //ID của phim trên IMDb (Internet Movie Database)
    String imdbId;

    @Field(name = "original_language")
    String originalLanguage;

    @Field(name = "homepage")
    String homepage;

    @Field(name = "is_deleted")
    Boolean isDeleted = false; //Use for soft delete :>
}
