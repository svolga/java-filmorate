package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidateException;
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
    public Film update(@Valid Film film) throws ValidateException {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return films.get(film.getId());
        }
        throw new ValidateException("Фильм c id = " + film.getId() + " не существует");
    }

    @Override
    public List<Film> getAll() {
        return List.copyOf(films.values());
    }

}
