package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDbStorage extends AbstractStorage<User> {

    List<User> findAllFriends(long id);

    List<User> findCommonFriends(long id, long friendId);

    void removeUserById(long userId);
}
