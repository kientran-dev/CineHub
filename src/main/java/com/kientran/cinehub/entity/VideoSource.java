package com.kientran.cinehub.entity;

import com.kientran.cinehub.enums.VideoQuality;
import com.kientran.cinehub.enums.AudioType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoSource {

    VideoQuality quality; // 720p, 1080p, 4K

    AudioType audioType; // ORIGINAL, VIETSUB, VIETNAMESE_DUB, VOICEOVER

    String url; // URL của file video

    List<SubtitleTrack> subtitleTracks; // Các track phụ đề

    String audioLanguage; // "vi", "ko", "en"

}
