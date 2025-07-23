package com.kientran.cinehub.entity;

import com.kientran.cinehub.enums.VoteType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "review_votes")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewVote extends BaseEntity {

    @Field(name = "user_id")
    String userId;

    @Field(name = "review_id")
    String reviewId;

    @Field(name = "vote_type")
    VoteType voteType; // UPVOTE or DOWNVOTE
}
