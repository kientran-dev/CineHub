package com.kientran.cinehub.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieRequest {
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
    String trailerUrl;
    List<Long> genreIds;
    List<Long> actorIds;
}