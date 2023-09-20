package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EventNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Slf4j
@Repository
public class EventDbStorageImpl implements EventDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Event create(Event event) {
        String sqlQuery = "INSERT INTO events (user_id, event_type, operation, entity_id)" +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"event_id"});
            ps.setLong(1, event.getUserId());
            ps.setString(2, event.getEventType().toString());
            ps.setString(3, event.getOperation().toString());
            ps.setLong(4, event.getEntityId());
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        Event newEvent = findById(id);
        log.info(newEvent.toString());
        return newEvent;
    }

    @Override
    public List<Event> getAll() {
        String sqlQuery = "SELECT * " +
                "FROM events " +
                "ORDER BY updated_at " +
                "LIMIT 100";

        return jdbcTemplate.query(sqlQuery, this::mapRowToEvent);
    }

    @Override
    public Event findById(long id) {
        String sqlQuery = "SELECT * " +
                "FROM events " +
                "WHERE event_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToEvent, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EventNotFoundException(String.format("Event с id = %d не найден", id));
        }
    }

    private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .entityId(rs.getLong("entity_id"))
                .userId(rs.getLong("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    @Override
    public List<Event> findByUserId(long userId) {
        String sqlQuery = "SELECT * " +
                "FROM events " +
                "WHERE user_id = ?" +
                "ORDER BY updated_at " +
                "LIMIT 100";
        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToEvent, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new EventNotFoundException(String.format("Feeds для пользователя с user_id = %d не найден", userId));
        }
    }

}
