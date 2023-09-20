package ru.yandex.practicum.filmorate.storage.db;

public interface LikeDbStorage<T, R> {
    boolean createLike(T source1, R source2);

    boolean removeLike(T source1, R source2);

    boolean isExistsLike(T source1, R source2);
}
