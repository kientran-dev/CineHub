package com.kientran.cinehub.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "favorites")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Favorite extends BaseEntity {
    @Field(name = "user_id")
    String userId;

    @Field(name = "content_id")
    String contentId;

    @Field(name = "is_favorite")
    Boolean isFavorite = true;
}
