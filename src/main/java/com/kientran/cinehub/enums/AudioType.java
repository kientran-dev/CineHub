package com.kientran.cinehub.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AudioType {
    ORIGINAL("Original", "Âm thanh gốc"),
    VIETSUB("Vietsub", "Phụ đề tiếng Việt"),
    VIETNAMESE_DUB("Vietnamese Dub", "Lồng tiếng tiếng Việt"),
    VOICEOVER("Voiceover", "Thuyết minh tiếng Việt");

    private final String code;
    private final String description;
}
