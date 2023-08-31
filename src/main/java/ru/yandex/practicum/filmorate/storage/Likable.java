package ru.yandex.practicum.filmorate.storage;

public interface Likable <T,R> {
    boolean addLike (T source1, R source2);
    boolean removeLike (T source1, R source2);
    boolean findLike (T source1, R source2);
}
