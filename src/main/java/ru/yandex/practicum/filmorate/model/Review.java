package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


@Data
@Builder
@AllArgsConstructor
public class Review {
    private long reviewId;

    @NonNull
    private Long filmId;

    @NonNull
    private Long userId;

    @NonNull
    private String content;

    @NonNull
    private Boolean isPositive;
    private Long useful;

    @JsonProperty("isPositive")
    public Boolean getIsPositive() {
        return isPositive;
    }
}
