package com.kientran.cinehub.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VoteType {
    UPVOTE("upvote"),
    DOWNVOTE("downvote");

    private final String type;

}
