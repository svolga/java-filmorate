package ru.yandex.practicum.filmorate.storage.db;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractStorageGetter;

import java.util.List;

public interface GenreDbStorage extends AbstractStorageGetter<Genre> {
    List<Genre> findByFilm(long filmId);

    void createFilmGenre(Film film);

    int removeFilmGenre(Film film);
}
