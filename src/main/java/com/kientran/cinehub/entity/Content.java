package com.kientran.cinehub.entity;

import com.kientran.cinehub.enums.ContentType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Document(collection = "contents")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Content extends BaseEntity {

    @Field(name = "title")
    String title;

    @Field(name = "original_title")
    String originalTitle;

    @Field(name = "genre_ids")
    List<String> genreIds;

    @Field(name = "overview")
    String overview;

    @Field(name = "poster_path")
    String posterPath;

    @Field(name = "backdrop_path")
    String backdropPath;

    @Field(name = "trailer_url")
    String trailerUrl;

    @Field(name = "content_type")
    ContentType contentType;

    @Field(name = "genres")
    Set<Genre> genres;

    @Field(name = "average_rating")
    Double averageRating = 0.0;

    @Field(name = "rating_count")
    Integer ratingCount = 0;

    @Field(name = "popularity")
    Double popularity;

    // =============== THÔNG TIN SẢN XUẤT ===============
    @Field(name = "release_date")
    LocalDate releaseDate;

    @Field(name = "production_year")
    Integer productionYear;

    @Field(name = "original_language")
    String originalLanguage;

    // =============== THÔNG TIN CAST & CREW ===============
    @Field(name = "director")
    String director;

    @Field(name = "cast")
    List<String> cast;

    @Field(name = "imdb_id")
    String imdbId;

    @Field(name = "tmdb_id")
    String tmdbId;

    @Field(name = "is_deleted")
    Boolean isDeleted = false;

    //Chỉ dành cho phim bộ
    @Field(name = "status")
    String status; // "Ongoing", "Completed", "Cancelled"

}