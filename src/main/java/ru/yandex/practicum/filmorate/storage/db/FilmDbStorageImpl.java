package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.util.Const;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
@Qualifier("filmDbStorageImpl")
public class FilmDbStorageImpl implements FilmDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final DirectorDbStorage directorDbStorage;

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

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(id);

        genreDbStorage.createFilmGenre(film);
        directorDbStorage.createFilmDirector(film);

        return findById(film.getId());
    }

    @Override
    public Film update(Film film) throws ValidateException {
        if (findById(film.getId()) == null) {
            throw new FilmNotFoundException("Фильм c id = " + film.getId() + " не существует");
        }

        Integer mpaId = film.getMpa() == null ? null : film.getMpa().getId();

        String sqlQuery = "UPDATE films " +
                "SET name = ?, release_date = ?, description = ?, duration = ?, rate = ?, rating_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, film.getName(), Date.valueOf(film.getReleaseDate()), film.getDescription(),
                film.getDuration(), film.getRate(), mpaId, film.getId());

        genreDbStorage.createFilmGenre(film);
        directorDbStorage.createFilmDirector(film);

        return findById(film.getId());
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpas m ON f.rating_id = m.rating_id";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);

        return getOtherLinks(films);
    }

    @Override
    public Film findById(long id) {
        String sqlQuery = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpas m ON f.rating_id = m.rating_id " +
                "WHERE f.film_id = ?";

        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            if (film != null)
                getOtherLinks(film);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Фильм с id = %d не найден", id));
        }
    }

    @Override
    public List<Film> findRecommendedFilms(long userId, long commonUserId) {
        String sqlQuery = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpas m ON f.rating_id = m.rating_id " +
                "LEFT JOIN likes l ON l.film_id = f.film_id " +
                "WHERE l.user_id = ? AND l.film_id NOT IN " +
                "(SELECT film_id FROM likes WHERE user_id = ? )";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, commonUserId, userId);

        return films.stream()
                .peek(this::getOtherLinks)
                .collect(Collectors.toList());
    }

    private void getOtherLinks(Film film) {
        List<Genre> genres = genreDbStorage.findByFilm(film.getId());
        List<Long> userIds = findLikedUsersByFilm(film.getId());
        List<Director> directors = directorDbStorage.findByFilm(film);
        film.getLikes().addAll(userIds);
        film.getGenres().addAll(genres);
        film.getDirectors().addAll(directors);
    }

    private List<Film> getOtherLinks(List<Film> films) {
        return films.stream()
                .peek(this::getOtherLinks)
                .collect(Collectors.toList());
    }

    private List<Long> findLikedUsersByFilm(long filmId) {
        String sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, filmId);
    }

    @Override
    public List<Film> findAllPopular(int count, Long genreId, Integer year) {
        log.info("count --> {}", count);
        log.info("genreId --> {}", genreId);
        log.info("year --> {}", year);

        List<Film> films;
        String selectionRules = "";
        String sqlQuery = "SELECT vs.cnt, m.name AS mpa_name, f.* FROM films f " +
                "LEFT JOIN  (SELECT film_id, COUNT(l.*) AS cnt FROM likes l " +
                "GROUP BY (film_id) ) vs " +
                "ON vs.film_id = f.film_id " +
                "LEFT JOIN mpas m ON f.rating_id = m.rating_id " +
                " %s " +
                "ORDER BY vs.cnt DESC " +
                "LIMIT ? ;";

        if (genreId == null) {
            if (year == null) {
                films = jdbcTemplate.query(String.format(sqlQuery, selectionRules),
                        this::mapRowToFilm, count);
            } else {
                selectionRules = "WHERE YEAR(release_date) = ?";
                films = jdbcTemplate.query(String.format(sqlQuery, selectionRules),
                        this::mapRowToFilm, year, count);
            }
        } else {
            if (year == null) {
                genreDbStorage.findById(genreId);
                selectionRules = "WHERE f.film_id IN (SELECT film_id FROM film_genres WHERE genre_id = ? )";
                films = jdbcTemplate.query(String.format(sqlQuery, selectionRules),
                        this::mapRowToFilm, genreId, count);
            } else {
                genreDbStorage.findById(genreId);
                selectionRules = "WHERE f.film_id IN (SELECT film_id FROM film_genres WHERE genre_id = ? " +
                        " AND YEAR(release_date) = ? )";
                films = jdbcTemplate.query(String.format(sqlQuery, selectionRules),
                        this::mapRowToFilm, genreId, year, count);
            }
        }
        return getOtherLinks(films);
    }

    @Override
    public List<Film> findDirectorsFilmsYearSorted(long id) {
        String sqlQuery = "SELECT * " +
                "FROM films f " +
                "LEFT JOIN  (SELECT film_id, COUNT(l.*) AS cnt FROM likes l " +
                "GROUP BY (film_id) ) vs " +
                "ON vs.film_id = f.film_id " +
                "LEFT JOIN (SELECT name AS mpa_name, rating_id  FROM mpas) as m ON f.rating_id = m.rating_id " +
                "LEFT JOIN film_directors fd ON f.film_id = fd.film_id " +
                "WHERE director_id = ? " +
                "ORDER BY f.release_date ";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id);

        return getOtherLinks(films);
    }

    @Override
    public List<Film> findCommonFilm(long userId, long friendId) {
        String sqlQuery =
                "WITH " +
                        "intersects AS ( " +
                        "SELECT film_id FROM likes WHERE user_id = ? " +
                        "INTERSECT SELECT film_id FROM likes WHERE user_id = ? ) " +
                        "SELECT f.*, m.name AS mpa_name " +
                        "FROM films f " +
                        "LEFT JOIN mpas m ON f.rating_id = m.rating_id " +
                        "RIGHT JOIN intersects its ON f.film_id = its.film_id ";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, friendId);
        return getOtherLinks(films);
    }

    public List<Film> findDirectorsFilmsLikeSorted(long id) {
        String sqlQuery = "SELECT * " +
                "FROM films f " +
                "LEFT JOIN  (SELECT film_id, COUNT(l.*) AS cnt FROM likes l " +
                "GROUP BY (film_id) ) vs " +
                "ON vs.film_id = f.film_id " +
                "LEFT JOIN (SELECT name AS mpa_name, rating_id  FROM mpas) as m ON f.rating_id = m.rating_id " +
                "LEFT JOIN film_directors fd ON f.film_id = fd.film_id " +
                "WHERE director_id = ? " +
                "ORDER BY vs.cnt DESC ";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id);

        return getOtherLinks(films);
    }

    @Override
    public long removeById(long filmId) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
        return filmId;
    }

    @Override
    public List<Film> findByFields(Set<String> fields, String query) {

        StringBuilder subQuery = new StringBuilder();
        List<Object> parameters = new ArrayList<>();
        String likeQuery = getLIkeQuery(query);
        parameters.add(likeQuery);

        if (fields.contains(Const.DIRECTOR_SEARCH) && fields.contains(Const.TITLE_SEARCH)) {
            parameters.add(likeQuery);
            subQuery.append(" AND (LOWER(f.name) LIKE ? OR LOWER(d.name) LIKE ?) ");
        } else if (fields.contains(Const.DIRECTOR_SEARCH)) {
            subQuery.append(" AND LOWER(d.name) LIKE ? ");
        } else if (fields.contains(Const.TITLE_SEARCH)) {
            subQuery.append(" AND LOWER(f.name) LIKE ? ");
        }

        String sqlQuery = "SELECT DISTINCT vs.cnt, m.name AS mpa_name, f.* " +
                "FROM films f LEFT JOIN  (SELECT film_id, COUNT(l.*) AS cnt FROM likes l GROUP BY (film_id) ) vs " +
                "ON vs.film_id = f.film_id " +
                "LEFT JOIN mpas m ON f.rating_id = m.rating_id " +
                "LEFT JOIN film_directors fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors d ON d.director_id = fd.director_id " +
                "WHERE 1 = 1 " + subQuery +
                "ORDER BY vs.cnt DESC";

        Object[] paramArray = parameters.toArray();
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, paramArray);
        return getOtherLinks(films);
    }

    private String getLIkeQuery(String query) {
        return "%" + query.toLowerCase() + "%";
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
}