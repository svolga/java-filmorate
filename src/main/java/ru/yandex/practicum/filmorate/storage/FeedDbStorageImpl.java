package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FeedNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
@Repository
public class FeedDbStorageImpl implements FeedDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Feed create(Feed feed) {
        String sqlQuery = "INSERT INTO feeds (user_id, event_type, operation, entity_id)" +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"feed_id"});
            ps.setLong(1, feed.getUserId());
            ps.setString(2, feed.getEventType().toString());
            ps.setString(3, feed.getOperation().toString());
            ps.setLong(4, feed.getEntityId());
            return ps;
        }, keyHolder);

        feed.setId(keyHolder.getKey().longValue());

        return findById(feed.getId());
    }

    @Override
    public List<Feed> getAll() {
        String sqlQuery = "SELECT * " +
                "FROM feeds " +
                "ORDER BY updated_at " +
                "LIMIT 100";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFeed);
    }

    @Override
    public Feed findById(long id) {
        String sqlQuery = "SELECT * " +
                "FROM feeds " +
                "WHERE feed_id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFeed, id);
        } catch (EmptyResultDataAccessException e) {
            throw new FeedNotFoundException(String.format("Feed с id = %d не найден", id));
        }
    }

    private Feed mapRowToFeed(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .id(rs.getLong("feed_id"))
                .entityId(rs.getLong("entity_id"))
                .userId(rs.getLong("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    @Override
    public List<Feed> findByUserId(long user_id) {
        String sqlQuery = "SELECT * " +
                "FROM feeds " +
                "WHERE user_id = ?" +
                "ORDER BY updated_at " +
                "LIMIT 100";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToFeed, user_id);
        } catch (EmptyResultDataAccessException e) {
            throw new FeedNotFoundException(String.format("Feeds для пользователя с user_id = %d не найден", user_id));
        }
    }

}
