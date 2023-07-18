package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.practicum.filmorate.model.Film;

@Service
public class FilmService implements Manager<Film> {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id;

    private int getNextId(){
        return ++id;
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Film update(Film user) {
        if (films.containsKey(user.getId())){
            films.put(user.getId(), user);
            return films.get(user.getId());
        }
        return null;
    }

    @Override
    public List<Film> getAll() {
        return List.copyOf(films.values());
    }
}
