package com.kientran.cinehub.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateWatchProgressRequest {

    @NotBlank(message = "Content ID không được để trống")
    String contentId;

    String seasonId; // Optional cho TV series

    String episodeId; // Optional cho TV series

    Integer seasonNumber; // Optional cho TV series

    Integer episodeNumber; // Optional cho TV series

    @NotNull(message = "Thời gian hiện tại không được để trống")
    @Min(value = 0, message = "Thời gian hiện tại phải >= 0")
    Long currentTimeSeconds;

    @NotNull(message = "Tổng thời lượng không được để trống")
    @Min(value = 1, message = "Tổng thời lượng phải > 0")
    Long totalDurationSeconds;
}