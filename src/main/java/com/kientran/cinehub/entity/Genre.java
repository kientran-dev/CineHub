package com.kientran.cinehub.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "genres")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre extends BaseEntity {

    @Field(name = "name_vi")
    String nameVi; // "Hành động", "Tâm lý", "Hài hước"

    @Field(name = "description")
    String description;

    @Field(name = "is_active")
    Boolean isActive = true;

    @Field(name = "sort_order")
    Integer sortOrder = 0; // Thứ tự hiển thị
}