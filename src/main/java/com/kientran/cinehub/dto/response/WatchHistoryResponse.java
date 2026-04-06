package com.kientran.cinehub.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WatchHistoryResponse {
    Long id;
    Long episodeVersionId;
    Long episodeId;
    String movieTitle;
    String episodeName;
    String versionType;
    Integer watchTime;
    Integer currentEpisode;
    LocalDateTime watchDate;
}