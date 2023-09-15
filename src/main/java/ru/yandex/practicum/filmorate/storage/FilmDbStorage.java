package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmDbStorage extends AbstractStorage<Film> {
    List<Film> findAllPopular(int count, Long genreId, Integer year);

    List<Film> findCommonFilm(long userId, long friendId);

    List<Film> findByFields(Set<String> fields, String query);

    List<Film> findDirectorsFilmsYearSorted(long id);

    List<Film> findDirectorsFilmsLikeSorted(long id);

    void removeFilm(long filmId);

    List<Film> getLikedFilms(long userId);
}
