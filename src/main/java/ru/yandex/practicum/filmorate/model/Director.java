package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@Validated
@AllArgsConstructor
public class Director {
    private final long id;
    @NonNull
    @NotBlank(message = "Заполните name")
    private final String name;
}
