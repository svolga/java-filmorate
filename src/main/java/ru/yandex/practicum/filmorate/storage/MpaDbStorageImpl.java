package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Qualifier("mpaDbStorage")
public class MpaDbStorageImpl implements AbstractStorageGetter<Mpa> {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAll() {
        String sqlQuery = "SELECT * FROM mpas";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Mpa findById(long id) {
        String sqlQuery = "SELECT * FROM mpas WHERE rating_id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException(String.format("Рейтинг с id = %d не найден", id));
        }
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("name"))
                .build();
    }
}
