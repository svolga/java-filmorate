package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.util.Const;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {

    private int id;

    @Email(message = "электронная почта должна содержать символ @")
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;

    @NonNull
    @NotBlank(message = "Логин не может быть пустой")
    private String login;

    @Setter
    private String name;

    @Past(message = "Дата рождения не может быть в будущем")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Const.DATE_FORMAT)
    @DateTimeFormat(pattern = Const.DATE_FORMAT)
    private LocalDate birthday;

    public String getName() {
        if (name == null || name.isEmpty()) {
            name = this.login;
        }
        return name;
    }
}
