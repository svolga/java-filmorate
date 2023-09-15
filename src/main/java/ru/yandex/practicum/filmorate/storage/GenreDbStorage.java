package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDbStorage extends AbstractStorageGetter<Genre> {
    List<Genre> findByFilm(long filmId);

    void createFilmGenre(Film film);

    int removeFilmGenre(Film film);
}
