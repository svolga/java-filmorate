package ru.yandex.practicum.filmorate.storage.db;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;
import ru.yandex.practicum.filmorate.storage.AbstractStorageRemover;

import java.util.List;
import java.util.Set;

public interface FilmDbStorage extends AbstractStorage<Film>, AbstractStorageRemover {
    List<Film> findAllPopular(int count, Long genreId, Integer year);

    List<Film> findCommonFilm(long userId, long friendId);

    List<Film> findByFields(Set<String> fields, String query);

    List<Film> findDirectorsFilmsYearSorted(long id);

    List<Film> findDirectorsFilmsLikeSorted(long id);

    List<Film> getLikedFilms(long userId);
}
