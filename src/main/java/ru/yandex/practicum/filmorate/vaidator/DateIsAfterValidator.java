package ru.yandex.practicum.filmorate.vaidator;

import ru.yandex.practicum.filmorate.exception.ValidateException;

import java.time.LocalDate;

public class DateIsAfterValidator implements Validator <LocalDate> {

    private final LocalDate minValue;

    public DateIsAfterValidator(LocalDate minValue) {
        this.minValue = minValue;
    }

    @Override
    public void validate(LocalDate value) throws ValidateException {
        if (value.isBefore(minValue)){
            throw new ValidateException("Дата " + value + " должна быть больше " + minValue);
        }
    }
}
