package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidateException;

import java.util.List;

public interface AbstractStorage<T> {

    T create(T t);

    T update(T t) throws ValidateException;

    List<T> getAll();

    T findById(long id);
}
