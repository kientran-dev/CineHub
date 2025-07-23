package com.kientran.cinehub.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentType {
    MOVIE("Movie", "Phim lẻ"),
    SERIES("Series", "Phim bộ"),
    DOCUMENTARY("Documentary", "Phim tài liệu"),
    ANIMATION("Animation", "Phim hoạt hình"),
    SHORT_FILM("Short Film", "Phim ngắn");

    private final String code;
    private final String description;
}
