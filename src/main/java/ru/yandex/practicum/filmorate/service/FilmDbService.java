package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmDbService {

    private final FilmDbStorage filmDbStorage;

    private final UserDbService userDbService;

    public FilmDbService(@Qualifier("filmDbStorageImpl") FilmDbStorage filmDbStorage, UserDbService userDbService) {
        this.filmDbStorage = filmDbStorage;
        this.userDbService = userDbService;
    }

    public Film create(Film film) {
        return filmDbStorage.create(film);
    }

    public Film update(Film film) throws ValidateException {
        return filmDbStorage.update(film);
    }

    public List<Film> getAll() {
        return filmDbStorage.getAll();
    }

    public Film findFilmById(long id) {
        return filmDbStorage.findById(id);
    }

    public void addLike(long id, long userId) {
        Film film = findFilmById(id);
        User user = userDbService.findUserById(userId);
        filmDbStorage.addLike(id, userId);
    }

    public void removeLike(long id, long userId) {
        Film film = findFilmById(id);
        User user = userDbService.findUserById(userId);
        filmDbStorage.removeLike(id, userId);
    }

    public List<Film> findAllPopular(long count) {
/*
        return filmDbStorage.getAll().stream()
                .sorted(Comparator.comparing(Film::getLikeCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
 */
        return null;
//        return filmDbStorage.findAllPopular(count);
    }

}
