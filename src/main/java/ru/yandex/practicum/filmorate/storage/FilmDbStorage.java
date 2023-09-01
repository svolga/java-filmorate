package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDbStorage extends AbstractStorage<Film>, Likable<Long, Long> {
    List<Film> findAllPopular(int count);
}
