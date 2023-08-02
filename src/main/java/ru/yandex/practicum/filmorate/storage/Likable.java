package ru.yandex.practicum.filmorate.storage;

public interface Likable <T> {

    void addLike(T t, int userId);
    void removeLike(T t, int userId);

}
