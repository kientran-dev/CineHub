package com.kientran.cinehub.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddToFavoriteRequest {

    @NotBlank(message = "Content ID không được để trống")
    String contentId;
}
