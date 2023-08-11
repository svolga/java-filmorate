package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

@Slf4j
@Service
public class FilmService {

    private final AbstractStorage<Film> filmStorage;
    private final UserService userService;

    public FilmService(AbstractStorage<Film> filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) throws ValidateException {
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film findFilmById(long id) {
        return filmStorage.findById(id);
    }

    public void addLike(long id, long userId) {
        Film film = findFilmById(id);
        User user = userService.findUserById(userId);
        film.getLikes().add(user.getId());
    }

    public void removeLike(long id, long userId) {
        Film film = findFilmById(id);
        User user = userService.findUserById(userId);
        film.getLikes().remove(user.getId());
    }

    public List<Film> findAllPopular(long count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparing(Film::getLikeCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
