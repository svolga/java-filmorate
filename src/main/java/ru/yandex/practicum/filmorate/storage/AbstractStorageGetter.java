package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface AbstractStorageGetter<T> {
    List<T> getAll();

    T findById(long id);
}
