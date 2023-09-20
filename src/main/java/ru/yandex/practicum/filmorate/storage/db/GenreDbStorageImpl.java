package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Qualifier("genreDbStorage")
public class GenreDbStorageImpl implements GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre findById(long id) {
        String sqlQuery = "SELECT * " +
                "FROM genres " +
                "WHERE genre_id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException(String.format("Жанр с id = %d не найден", id));
        }
    }

    @Override
    public List<Genre> findByFilm(long filmId) {
        String sqlQuery = "SELECT g.* " +
                "FROM film_genres fg " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE film_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    @Override
    public void createFilmGenre(Film film) {
        removeFilmGenre(film);

        List<Genre> genres = film.getGenres();
        if (genres != null) {
            String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            Set<Integer> genreIds = genres.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            for (Integer genreId : genreIds) {
                jdbcTemplate.update(sqlQuery, film.getId(), genreId);
            }
        }
    }

    @Override
    public int removeFilmGenre(Film film) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        return jdbcTemplate.update(sqlQuery, film.getId());
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }

}
