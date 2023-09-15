package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class UserDbService {

    @Qualifier("userDbStorageImpl")
    private final UserDbStorage userDbStorage;

    private final FriendDbStorage friendDbStorage;
    private final FeedDbStorage feedDbStorage;

    public User create(@Valid User user) {
        return userDbStorage.create(user);
    }

    public User update(@Valid User user) throws ValidateException {
        return userDbStorage.update(user);
    }

    public List<User> getAll() {
        return userDbStorage.getAll();
    }

    public User findUserById(long id) {
        return userDbStorage.findById(id);
    }

    public void addFriend(long id, long friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        friendDbStorage.createFriend(id, friendId);
    }

    public void removeFriend(long id, long friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        friendDbStorage.removeFriend(id, friendId);
    }

    public List<User> findAllFriends(long id) {
        User user = findUserById(id);
        List<User> friends = userDbStorage.findAllFriends(id);
        return friends;
    }

    public List<User> findCommonFriends(long id, long otherId) {
        User user = findUserById(id);
        User other = findUserById(otherId);
        List<User> friends = userDbStorage.findCommonFriends(id, otherId);
        return friends;
    }

    public void removeUserById(long userId) {
        userDbStorage.removeUserById(userId);
    }
}
