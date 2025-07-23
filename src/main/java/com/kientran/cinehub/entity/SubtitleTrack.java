package com.kientran.cinehub.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubtitleTrack {

    String language; // "vi", "en", "ko", "zh"

    String languageLabel; // "Tiếng Việt", "English", "한국어"

    String url; // URL file subtitle (.srt, .vtt)

}