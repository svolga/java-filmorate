package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.annotation.CustomAfterDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import ru.yandex.practicum.filmorate.util.Const;

@Data
public class Film {

    private int id;

    @NotBlank
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @DateTimeFormat(pattern = Const.DATE_FORMAT)
    @CustomAfterDate(message = "Дата должна быть больше " + Const.MIN_FILM_DATE, minDate = Const.MIN_FILM_DATE, formatDate = Const.DATE_FORMAT)
    private LocalDate releaseDate;

    @Min(value = 1, message = "Продолжительность фильма должна быть положительной")
    private int duration;

}
