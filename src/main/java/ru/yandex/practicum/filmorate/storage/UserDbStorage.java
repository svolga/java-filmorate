package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

public interface UserDbStorage extends AbstractStorage<User>, Friendable<Long, User> {
}
