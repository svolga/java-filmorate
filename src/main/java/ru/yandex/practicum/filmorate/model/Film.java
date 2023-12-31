package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.annotation.CustomAfterDate;
import ru.yandex.practicum.filmorate.util.Const;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@Validated
@AllArgsConstructor
public class Film {

    @Singular
    @JsonIgnore
    private final Set<Long> likes = new HashSet<>();

    private final Mpa mpa;
    private final List<Genre> genres = new ArrayList<>();
    private final List<Director> directors = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();

    private long id;

    private double rate;

    @NonNull
    @NotBlank(message = "Заполните name")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @DateTimeFormat(pattern = Const.DATE_FORMAT)
    @CustomAfterDate(message = "Дата должна быть больше " + Const.MIN_FILM_DATE, minDate = Const.MIN_FILM_DATE, formatDate = Const.DATE_FORMAT)
    private LocalDate releaseDate;

    @Min(value = 1, message = "Продолжительность фильма должна быть положительной")
    private int duration;

    public int getLikeCount() {
        return likes.size();
    }

    public void setMpaRating(int mpaId) {
        mpa.setId(mpaId);
    }

}
