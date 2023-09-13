package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.util.Const.SEARCH_FILM;

@Slf4j
@Service
public class FilmDbService {

    private final FilmDbStorage filmDbStorage;

    private final UserDbStorage userDbStorage;
    private final LikeDbStorage<Long, Long> likeDbStorage;

    public FilmDbService(@Qualifier("filmDbStorageImpl") FilmDbStorage filmDbStorage, UserDbStorage userDbStorage, LikeDbStorage<Long, Long> likeDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
        this.likeDbStorage = likeDbStorage;
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
        User user = userDbStorage.findById(userId);
        likeDbStorage.addLike(id, userId);
    }

    public void removeLike(long id, long userId) {
        Film film = findFilmById(id);
        User user = userDbStorage.findById(userId);
        likeDbStorage.removeLike(id, userId);
    }

    public List<Film> findAllPopular(int count) {
        return filmDbStorage.findAllPopular(count);
    }

    public List<Film> findByTitleAndDirector(String query, String by) {
        String[] params = by.split(",");
        for (String param : params) {
            if (!SEARCH_FILM.contains(param)) {
                throw new IncorrectParameterException("Неверное значение в параметре by");
            }
        }

        Set<String> fields = Set.of(params);
        return filmDbStorage.findByFields(fields, query);
    }

}
