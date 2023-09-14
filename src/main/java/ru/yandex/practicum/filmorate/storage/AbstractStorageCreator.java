package ru.yandex.practicum.filmorate.storage;

public interface AbstractStorageCreator<T> {
    T create(T t);
}
