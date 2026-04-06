package com.kientran.cinehub.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponse {
    Long id;
    String title;
    String englishTitle;
    String thumbnail;
    String poster;
    String description;
    String director;
    Integer releaseYear;
    Integer duration;
    String country;
    String status;
    String type;
    Double imdbScore;
    Double averageRating;
    Integer totalRatings;
    Set<GenreResponse> genres;
    List<EpisodeResponse> episodes;
    List<ActorResponse> actors;
}