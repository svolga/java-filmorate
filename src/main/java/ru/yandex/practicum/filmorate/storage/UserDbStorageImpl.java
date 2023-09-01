package ru.yandex.practicum.filmorate.storage;

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

@Repository
@Qualifier("userDbStorage")
public class UserDbStorageImpl implements UserDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String getTableName() {
        return "`user`";
    }

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO " + this.getTableName() + " (`login`, `name`, `email`, `birthday`) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) throws ValidateException {

        if (findById(user.getId()) == null) {
            throw new UserNotFoundException("Пользователь c id = " + user.getId() + " не существует");
        }

        String sqlQuery = "UPDATE " + this.getTableName() + " SET login = ? , name = ?, email = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getLogin(), user.getName(), user.getEmail(), Date.valueOf(user.getBirthday()), user.getId());
        return findById(user.getId());
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT * FROM " + this.getTableName();
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User findById(long id) {
        String sqlQuery = "SELECT * FROM " + this.getTableName() + " WHERE user_id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("User с id = %d не найден", id));
        }
    }

    @Override
    public boolean createFriend(Long id, Long friendId) {
        if (!isHasFriend(id, friendId)) {
            String sqlQuery = "INSERT INTO user_friend (user_id, friend_id) values (?, ?)";
            return jdbcTemplate.update(sqlQuery, id, friendId) > 0;
        }
        return false;
    }

    @Override
    public boolean removeFriend(Long id, Long friendId) {
        String sqlQuery = "DELETE FROM user_friend WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sqlQuery, id, friendId) > 0;
    }

    @Override
    public boolean isHasFriend(Long id, Long friendId) {
        return jdbcTemplate.queryForObject("SELECT EXISTS(SELECT FROM user_friend WHERE user_id = ? AND friend_id = ?)",
                Boolean.class, id, friendId);
    }

    @Override
    public List<User> findAllFriends(Long id) {
        String sqlQuery = "SELECT * FROM " + this.getTableName() +
                "WHERE user_id IN (SELECT friend_id FROM user_friend WHERE user_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public List<User> findCommonFriends(Long id, Long friendId) {
        String sqlQuery = "SELECT * FROM `user` " +
                "WHERE user_id IN" +
                " ( SELECT friend_id FROM user_friend WHERE user_id = ? " +
                "INTERSECT SELECT friend_id FROM user_friend WHERE user_id = ? )";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, friendId);
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
