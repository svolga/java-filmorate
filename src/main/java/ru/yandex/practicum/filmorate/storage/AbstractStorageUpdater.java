package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidateException;

public interface AbstractStorageUpdater<T> {
    T update(T t) throws ValidateException;
}
