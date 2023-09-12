package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
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
    private final GenreDbStorage genreDbStorage;
    private final LikeDbStorage<Long, Long> likeDbStorage;

    public FilmDbStorageImpl(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage, LikeDbStorage<Long, Long> likeDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
        this.likeDbStorage = likeDbStorage;
    }

    @Override
    public Film create(Film film) {

        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
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
        genreDbStorage.createFilmGenre(film);

        return findById(film.getId());
    }

    @Override
    public Film update(Film film) throws ValidateException {
        if (findById(film.getId()) == null) {
            throw new FilmNotFoundException("Фильм c id = " + film.getId() + " не существует");
        }

        int mpaId = film.getMpa() == null ? null : film.getMpa().getId();

        String sqlQuery = "UPDATE films " +
                "SET name = ?, release_date = ?, description = ?, duration = ?, rate = ?, rating_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, film.getName(), Date.valueOf(film.getReleaseDate()), film.getDescription(),
                film.getDuration(), film.getRate(), mpaId, film.getId());

        genreDbStorage.createFilmGenre(film);

        return findById(film.getId());
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpas m ON f.rating_id = m.rating_id";

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
                "FROM films f " +
                "LEFT JOIN mpas m ON f.rating_id = m.rating_id " +
                "WHERE f.film_id = ?";

        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            getOtherLinks(film);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Фильм с id = %d не найден", id));
        }
    }

    private void getOtherLinks(Film film) {
        List<Genre> genres = genreDbStorage.findByFilm(film.getId());
        List<Long> userIds = findLikedUsersByFilm(film.getId());
        film.getLikes().addAll(userIds);
        film.getGenres().addAll(genres);
    }

    private List<Long> findLikedUsersByFilm(long filmId) {
        String sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
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
                .mpa(Mpa.builder().id(rs.getInt("rating_id")).name(rs.getString("mpa_name")).build())
                .build();
    }

    @Override
    public List<Film> findAllPopular(int count) {

        log.info("count --> {}", count);

        String sqlQuery = "SELECT vs.cnt, m.name AS mpa_name, f.* FROM films f " +
                "LEFT JOIN  (SELECT film_id, COUNT(l.*) AS cnt FROM likes l " +
                "GROUP BY (film_id) ) vs " +
                "ON vs.film_id = f.film_id " +
                "LEFT JOIN mpas m ON f.rating_id = m.rating_id " +
                "ORDER BY vs.cnt DESC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);

        films = films.stream()
                .map(film -> {
                    getOtherLinks(film);
                    return film;
                })
                .collect(Collectors.toList());

        return films;
    }
}
