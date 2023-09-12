package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

        return findById(keyHolder.getKey().longValue());
    }

    @Override
    public Director update(Director director) throws ValidateException {
        if (findById(director.getId()) == null) {
            throw new FilmNotFoundException("Режиссёр c id = " + director.getId() + " не существует"); //TODO Сменить эксепщн
        }
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
            throw new FilmNotFoundException(String.format("Режиссёр с id = %d не найден", id)); // TODO change exception
        }
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public void deleteById(long id) {
        if (findById(id) == null) {
            throw new FilmNotFoundException("Режиссёр c id = " + id + " не существует"); //TODO Сменить эксепщн
        }
        String sqlQuery = "DELETE FROM directors " +
                "WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }
}
