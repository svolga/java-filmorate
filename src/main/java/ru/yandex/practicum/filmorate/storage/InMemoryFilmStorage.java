package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements AbstractStorage<Film> {

    private final Map<Long, Film> films = new HashMap<>();
    private long id;

    private long getNextId() {
        return ++id;
    }

    @Override
    public Film create(@Valid Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Film update(@Valid Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return films.get(film.getId());
        }
        throw new FilmNotFoundException(String.format("Фильм с id = %d не найден", film.getId()));
    }

    @Override
    public List<Film> getAll() {
        return List.copyOf(films.values());
    }

    @Override
    public Film findById(long id) {
        return films.entrySet().stream()
                .filter(filmEntry -> filmEntry.getKey() == id)
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id = %d не найден", id)));
    }

}
