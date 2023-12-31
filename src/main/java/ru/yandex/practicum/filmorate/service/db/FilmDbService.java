package ru.yandex.practicum.filmorate.service.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.db.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.util.Const.SEARCH_FILM;

@Slf4j
@AllArgsConstructor
@Service
public class FilmDbService {

    @Qualifier("filmDbStorageImpl")
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final LikeDbStorage<Long, Long> likeDbStorage;
    private final DirectorDbStorage directorDbStorage;

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
        findFilmById(id);
        userDbStorage.findById(userId);
        likeDbStorage.createLike(id, userId);
    }

    public void removeLike(long id, long userId) {
        findFilmById(id);
        userDbStorage.findById(userId);
        likeDbStorage.removeLike(id, userId);
    }

    public List<Film> findAllPopular(int count, Long genreId, Integer year) {
        return filmDbStorage.findAllPopular(count, genreId, year);
    }

    public List<Film> findCommonFilm(long userId, long friendId) {
        userDbStorage.findById(userId);
        userDbStorage.findById(friendId);
        return filmDbStorage.findCommonFilm(userId, friendId);
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

    public List<Film> findDirectorsFilms(long id, String sortBy) {
        directorDbStorage.findById(id);
        if (sortBy.equals("year")) {
            return filmDbStorage.findDirectorsFilmsYearSorted(id);
        } else if (sortBy.equals("likes")) {
            return filmDbStorage.findDirectorsFilmsLikeSorted(id);
        } else {
            throw new IncorrectParameterException("Введён неправильный параметр для сортировки фильмов." +
                    " Возможные параметры для выбора сортировки: year и likes");
        }
    }

    public void removeFilmById(long filmId) {
        findFilmById(filmId);
        filmDbStorage.removeById(filmId);
    }
}

