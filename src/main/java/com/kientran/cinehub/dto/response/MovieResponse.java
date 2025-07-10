package com.kientran.cinehub.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponse {
    String id;
    String title;
    String description;
    String posterUrl;
    String trailerUrl;
    String releaseDate;
    String runtime;
    String director;
    String cast;
    String language;
    Double rating;
    Integer voteCount;
    Boolean isUpcoming;
    Boolean isPopular;
    Boolean isTopRated;

}
