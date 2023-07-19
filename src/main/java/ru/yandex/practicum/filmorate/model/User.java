package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.util.Const;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;

import java.time.LocalDate;

@Data
@Builder
@Validated
@AllArgsConstructor
public class User {

    private int id;

    @Email(message = "Электронная почта должна содержать символ @")
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;

    @NonNull
    @NotBlank(message = "Логин не может быть пустой")
    private String login;

    @NonNull
    @Setter
    private String name;

    @Past(message = "Дата рождения не может быть в будущем")
    @DateTimeFormat(pattern = Const.DATE_FORMAT)
    private LocalDate birthday;

    public String getName() {
        if (name == null || name.isEmpty()) {
            name = this.login;
        }
        return name;
    }
}
