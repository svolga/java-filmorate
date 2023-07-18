package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

@Service
public class FilmService implements Manager<Film> {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id;

    private int getNextId() {
        return ++id;
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Film update(Film film) throws ValidateException {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return films.get(film.getId());
        }
        throw new ValidateException("Фильм с id = " + film.getId() + " не существует");
    }

    @Override
    public List<Film> getAll() {
        return List.copyOf(films.values());
    }
}
