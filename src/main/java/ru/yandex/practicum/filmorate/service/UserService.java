package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    @Qualifier("userStorage")
    private final AbstractStorage<User> userStorage;

    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(@Valid User user) {
        return userStorage.create(user);
    }

    public User update(@Valid User user) throws ValidateException {
        return userStorage.update(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User findUserById(long id) {
        return userStorage.findById(id);
    }

    public void addFriend(long id, long friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
    }

    public void removeFriend(long id, long friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
    }

    public List<User> findAllFriends(long id) {
        User user = findUserById(id);

        List<User> friends = user.getFriends().stream()
                .map(this::findUserById)
                .collect(Collectors.toList());

        return friends;
    }

    public List<User> findCommonFriends(long id, long otherId) {
        User user = findUserById(id);
        User other = findUserById(otherId);

        Set<Long> retainFriends = new HashSet<>(user.getFriends());
        retainFriends.retainAll(other.getFriends());
        return retainFriends.stream()
                .map(this::findUserById)
                .collect(Collectors.toList());
    }

}
