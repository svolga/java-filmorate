package ru.yandex.practicum.filmorate.storage.db;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;
import ru.yandex.practicum.filmorate.storage.AbstractStorageRemover;

import java.util.List;

public interface DirectorDbStorage extends AbstractStorage<Director>, AbstractStorageRemover {
    void createFilmDirector(Film film);

    List<Director> findByFilm(Film film);
}
