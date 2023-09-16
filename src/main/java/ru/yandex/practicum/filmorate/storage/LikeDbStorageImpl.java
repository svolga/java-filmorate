package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

@AllArgsConstructor
@Repository
public class LikeDbStorageImpl implements LikeDbStorage<Long, Long> {

    private final JdbcTemplate jdbcTemplate;
    private final FeedDbStorage feedDbStorage;

    @Override
    public boolean addLike(Long filmId, Long userId) {
        if (!isExistsLike(filmId, userId)) {
            String sqlQuery = "INSERT INTO likes (film_id, user_id) values (?, ?)";
            if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
                feedDbStorage.create(Feed.builder().userId(userId).entityId(filmId).eventType(EventType.LIKE).operation(Operation.ADD).build());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        if (jdbcTemplate.update(sqlQuery, filmId, userId) > 0) {
            feedDbStorage.create(Feed.builder().userId(userId).entityId(filmId).eventType(EventType.LIKE).operation(Operation.REMOVE).build());
            return true;
        }
        return false;
    }

    @Override
    public boolean isExistsLike(Long filmId, Long userId) {
        String sqlQuery = "SELECT EXISTS(SELECT * FROM likes WHERE film_id = ? AND user_id = ?)";
        Boolean result = jdbcTemplate.queryForObject(sqlQuery, Boolean.class, filmId, userId);
        return result != null ? result : false;
    }

}
