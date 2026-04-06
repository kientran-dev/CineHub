package com.kientran.cinehub.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EpisodeVersionRequest {
    Long episodeId;
    String videoUrl;
    String type; // e.g. "VIETSUB", "THUYET_MINH", "LONG_TIENG"
}
