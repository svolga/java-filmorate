package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDbStorage extends AbstractStorage<User> {

    public List<User> findAllFriends(long id);

    public List<User> findCommonFriends(long id, long friendId);
}
