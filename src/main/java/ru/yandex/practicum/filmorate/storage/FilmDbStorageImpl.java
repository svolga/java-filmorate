package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.SQLException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("filmDbStorageImpl")
public class FilmDbStorageImpl implements FilmDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorageImpl genreDbStorageImpl;

    public FilmDbStorageImpl(JdbcTemplate jdbcTemplate, GenreDbStorageImpl genreDbStorageImpl) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorageImpl = genreDbStorageImpl;
    }

    @Override
    public Film create(Film film) {

        String sqlQuery = "INSERT INTO film (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null, Types.INTEGER);
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        createFilmGenre(film);

        return findById(film.getId());
    }

    @Override
    public Film update(Film film) throws ValidateException {
        if (findById(film.getId()) == null) {
            throw new FilmNotFoundException("Фильм c id = " + film.getId() + " не существует");
        }

        Integer mpaId = film.getMpa() == null ? null : film.getMpa().getId();

        String sqlQuery = "UPDATE film " +
                "SET name = ?, release_date = ?, description = ?, duration = ?, rate = ?, mpa_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, film.getName(), Date.valueOf(film.getReleaseDate()), film.getDescription(),
                film.getDuration(), film.getRate(), mpaId, film.getId());

        removeFilmGenre(film);
        createFilmGenre(film);

        return findById(film.getId());
    }

    private int removeFilmGenre(Film film) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        return jdbcTemplate.update(sqlQuery, film.getId());
    }

    private void createFilmGenre(Film film) {

        List<Genre> genres = film.getGenres();
        String sqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

        for (Genre genre : genres) {
            jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
        }

    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.*, m.name AS mpa_name " +
                "FROM film f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);

        return films.stream()
                .map(film -> {
                    getOtherLinks(film);
                    return film;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Film findById(long id) {
        String sqlQuery = "SELECT f.*, m.name AS mpa_name " +
                "FROM film f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";

        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            getOtherLinks(film);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("Фильм с id = %d не найден", id));
        }
    }

    private void getOtherLinks(Film film) {
        List<Genre> genres = genreDbStorageImpl.findByFilm(film.getId());
        List<Long> userIds = findLikedUsersByFilm(film.getId());
        film.getLikes().addAll(userIds);
        film.getGenres().addAll(genres);
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        if (!isExistsLike(filmId, userId)) {
            String sqlQuery = "INSERT INTO `like` (film_id, user_id) values (?, ?)";
            return jdbcTemplate.update(sqlQuery, filmId, userId) > 0;
        }
        return false;
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM `like` WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId, userId) > 0;
    }

    @Override
    public boolean isExistsLike(Long filmId, Long userId) {
        String sqlQuery = "SELECT EXISTS(SELECT * FROM `like` WHERE film_id = ? AND user_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, filmId, userId);
    }

    private List<Long> findLikedUsersByFilm(long filmId) {
        String sqlQuery = "SELECT user_id FROM `like` WHERE film_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, filmId);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .rate(rs.getDouble("rate"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder().id(rs.getInt("mpa_id")).name(rs.getString("mpa_name")).build())
                .build();
    }

    @Override
    public List<Film> findAllPopular(int count) {

        log.info("count --> {}", count);

        String sqlQuery = "SELECT f.*, m.name AS mpa_name " +
                "FROM (SELECT film_id, COUNT(l.*) AS cnt " +
                "FROM `like` l " +
                "GROUP BY (film_id) " +
                "ORDER BY cnt DESC LIMIT ? ) vs " +
                "LEFT JOIN film f ON f.film_id = vs.film_id " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id ";

        log.info("before query ");
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        log.info("after query ");

        return films.stream()
                .map(film -> {
                    getOtherLinks(film);
                    return film;
                })
                .collect(Collectors.toList());

    }
}
