package com.kientran.cinehub.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String director;
    Integer releaseYear;
    String country;
    String status;
    String type;
    Double imdb;
    Set<GenreResponse> genres;
}