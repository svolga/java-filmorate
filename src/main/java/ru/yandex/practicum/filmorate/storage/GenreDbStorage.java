package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDbStorage extends AbstractStorageGetter<Genre> {
    public List<Genre> findByFilm(long filmId);
}
