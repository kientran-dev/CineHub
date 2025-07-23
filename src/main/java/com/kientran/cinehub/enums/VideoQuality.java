package com.kientran.cinehub.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VideoQuality {
    SD_360P("360p", 360, 500),
    SD_480P("480p", 480, 800),
    HD_720P("720p", 720, 1500),
    FHD_1080P("1080p", 1080, 3000),
    QHD_1440P("1440p", 1440, 6000),
    UHD_4K("4K", 2160, 15000);

    private final String label;
    private final Integer height;
    private final Integer recommendedBitrate; // kbps
}
