package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorDbStorage extends AbstractStorage<Director> {
    void deleteById(long id);

    void createFilmDirector(Film film);

    List<Director> findByFilm(Film film);
}
