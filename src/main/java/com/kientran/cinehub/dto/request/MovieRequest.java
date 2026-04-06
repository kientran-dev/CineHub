package com.kientran.cinehub.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String director;
    Integer releaseYear;
    String country;
    String status;
    String type;
    Double imdbScore;
}