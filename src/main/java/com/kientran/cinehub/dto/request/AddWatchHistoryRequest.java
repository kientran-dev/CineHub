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
public class AddWatchHistoryRequest {

    @NotBlank(message = "Movie ID không được để trống")
    String movieId;

    @NotNull(message = "Thời gian đã xem không được để trống")
    @Min(value = 0, message = "Thời gian đã xem phải >= 0")
    Long watchedDurationSeconds;

    @NotNull(message = "Tổng thời lượng không được để trống")
    @Min(value = 1, message = "Tổng thời lượng phải > 0")
    Long totalDurationSeconds;
}