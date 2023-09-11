package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class FriendDBStorageImpl implements FriendDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendDBStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public boolean createFriend(long id, long friendId) {
        if (!isHasFriend(id, friendId)) {
            String sqlQuery = "INSERT INTO user_friends (user_id, friend_id) values (?, ?)";
            return jdbcTemplate.update(sqlQuery, id, friendId) > 0;
        }
        return false;
    }

    @Override
    public boolean removeFriend(long id, long friendId) {
        String sqlQuery = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sqlQuery, id, friendId) > 0;
    }

    @Override
    public boolean isHasFriend(long id, long friendId) {
        return jdbcTemplate.queryForObject("SELECT EXISTS(SELECT FROM user_friends WHERE user_id = ? AND friend_id = ?)",
                Boolean.class, id, friendId);
    }

}
