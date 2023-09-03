package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikeDbStorageImpl implements LikeDbStorage <Long, Long> {

    private final JdbcTemplate jdbcTemplate;

    public LikeDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        if (!isExistsLike(filmId, userId)) {
            String sqlQuery = "INSERT INTO likes (film_id, user_id) values (?, ?)";
            return jdbcTemplate.update(sqlQuery, filmId, userId) > 0;
        }
        return false;
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId, userId) > 0;
    }

    @Override
    public boolean isExistsLike(Long filmId, Long userId) {
        String sqlQuery = "SELECT EXISTS(SELECT * FROM likes WHERE film_id = ? AND user_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, filmId, userId);
    }

}
