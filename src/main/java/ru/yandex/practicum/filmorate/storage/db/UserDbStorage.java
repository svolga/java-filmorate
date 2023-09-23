package ru.yandex.practicum.filmorate.storage.db;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.List;

public interface UserDbStorage extends AbstractStorage<User> {

    List<User> findAllFriends(long id);

    List<User> findCommonFriends(long id, long friendId);

    List<Long> findCommonUsersIds(long id);

    void removeUserById(long userId);
}
