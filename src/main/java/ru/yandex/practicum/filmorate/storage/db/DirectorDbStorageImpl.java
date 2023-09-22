package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class DirectorDbStorageImpl implements DirectorDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director create(Director director) {

        String sqlQuery = "INSERT INTO directors (name) " +
                "VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return findById(id);
    }

    @Override
    public Director update(Director director) throws ValidateException {
        String sqlQuery = "UPDATE directors " +
                "SET name = ? " +
                "WHERE director_id = ?";

        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());

        return findById(director.getId());
    }


    @Override
    public List<Director> getAll() {
        String sqlQuery = "SELECT *" +
                "FROM directors ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Director findById(long id) {
        String sqlQuery = "SELECT * " +
                "FROM directors " +
                "WHERE director_id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException(String.format("Режиссёр с id = %d не найден", id));
        }
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public long removeById(long id) {
        String sqlQuery = "DELETE FROM directors " +
                "WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, id);
        return id;
    }

    @Override
    public void createFilmDirector(Film film) {
        removeFilmDirector(film);

        List<Director> directors = film.getDirectors().stream()
                .distinct()
                .collect(Collectors.toList());

        if (!directors.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    "MERGE INTO film_directors (film_id, director_id) VALUES (?, ?)",
                    new BatchPreparedStatementSetter() {

                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, film.getId());
                            ps.setLong(2, directors.get(i).getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return directors.size();
                        }
                    });
        }
    }

    @Override
    public List<Director> findByFilm(Film film) {
        String sqlQuery = "SELECT * " +
                "FROM film_directors fd " +
                "LEFT JOIN directors d ON d.director_id = fd.director_id " +
                "WHERE film_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector, film.getId());
    }

    private void removeFilmDirector(Film film) {
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());
    }
}
