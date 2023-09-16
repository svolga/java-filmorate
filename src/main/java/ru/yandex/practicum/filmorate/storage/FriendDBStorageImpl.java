package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

@AllArgsConstructor
@Repository
public class FriendDBStorageImpl implements FriendDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FeedDbStorage feedDbStorage;

    @Override
    public boolean createFriend(long id, long friendId) {
        if (!isHasFriend(id, friendId)) {
            String sqlQuery = "INSERT INTO user_friends (user_id, friend_id) values (?, ?)";
            if (jdbcTemplate.update(sqlQuery, id, friendId) > 0) {
                feedDbStorage.create(Feed.builder().userId(id).entityId(friendId).eventType(EventType.FRIEND).operation(Operation.ADD).build());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeFriend(long id, long friendId) {
        String sqlQuery = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        if (jdbcTemplate.update(sqlQuery, id, friendId) > 0) {
            feedDbStorage.create(Feed.builder().userId(id).entityId(friendId).eventType(EventType.FRIEND).operation(Operation.REMOVE).build());
            return true;
        }
        return false;
    }

    @Override
    public boolean isHasFriend(long id, long friendId) {
        Boolean result = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT FROM user_friends WHERE user_id = ? AND friend_id = ?)",
                Boolean.class, id, friendId);
        return result != null ? result : false;
    }

}
