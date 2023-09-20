package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
@AllArgsConstructor
@Qualifier("userDbStorage")
public class UserDbStorageImpl implements UserDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO users (login, name, email, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return findById(id);
    }

    @Override
    public User update(User user) throws ValidateException {

        if (findById(user.getId()) == null) {
            throw new UserNotFoundException("Пользователь c id = " + user.getId() + " не существует");
        }

        String sqlQuery = "UPDATE users SET login = ? , name = ?, email = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getLogin(), user.getName(), user.getEmail(), Date.valueOf(user.getBirthday()), user.getId());
        return findById(user.getId());
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User findById(long id) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("User с id = %d не найден", id));
        }
    }

    @Override
    public List<User> findAllFriends(long id) {
        String sqlQuery = "SELECT * FROM users " +
                "WHERE user_id IN (SELECT friend_id FROM user_friends WHERE user_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public List<User> findCommonFriends(long id, long friendId) {
        String sqlQuery = "SELECT * FROM users " +
                "WHERE user_id IN" +
                " ( SELECT friend_id FROM user_friends WHERE user_id = ? " +
                "INTERSECT SELECT friend_id FROM user_friends WHERE user_id = ? )";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, friendId);
    }

    @Override
    public void removeUserById(long userId) {
        if (findById(userId) == null) {
            throw new UserNotFoundException("User с id = " + userId + " не найден");
        }
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, userId);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

}
