package ru.yandex.practicum.filmorate.service.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@AllArgsConstructor
@Service
public class UserDbService {

    @Qualifier("userDbStorageImpl")
    private final UserDbStorage userDbStorage;
    private final FriendDbStorage friendDbStorage;
    private final FilmDbStorage filmDbStorage;

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
        findUserById(id);
        findUserById(friendId);
        friendDbStorage.createFriend(id, friendId);
    }

    public void removeFriend(long id, long friendId) {
        findUserById(id);
        findUserById(friendId);
        friendDbStorage.removeFriend(id, friendId);
    }

    public List<User> findAllFriends(long id) {
        findUserById(id);
        return userDbStorage.findAllFriends(id);
    }

    public List<User> findCommonFriends(long id, long otherId) {
        findUserById(id);
        findUserById(otherId);
        return userDbStorage.findCommonFriends(id, otherId);
    }

    public void removeUserById(long userId) {
        userDbStorage.removeUserById(userId);
    }

    public List<Film> findRecommendations(long id) {
        findUserById(id);

        Set<Film> recommendations = new HashSet<>();
        List<Long> commonUsersIds = userDbStorage.findCommonUsersIds(id);

        for (Long commonUserId : commonUsersIds) {
            recommendations.addAll(filmDbStorage.findRecommendedFilms(id, commonUserId));
        }

        return new ArrayList<>(recommendations);
    }

}
