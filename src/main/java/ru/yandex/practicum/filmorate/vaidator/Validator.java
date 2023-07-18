package ru.yandex.practicum.filmorate.vaidator;

import ru.yandex.practicum.filmorate.exception.ValidateException;

public interface Validator<T> {
    void validate(T value) throws ValidateException;
}
