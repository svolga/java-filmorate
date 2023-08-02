package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Singular;
import lombok.Setter;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.annotation.CustomAfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import ru.yandex.practicum.filmorate.util.Const;

@Data
@Builder
@Validated
@AllArgsConstructor
public class Film {

    @Singular
    private final Set<Long> likes = new HashSet<>();

    private long id;

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

}
