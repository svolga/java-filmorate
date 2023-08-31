package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Friendable<T, R> {
    boolean createFriend(T id, T friendId);
    boolean removeFriend(T id, T friendId);
    boolean isHasFriend(T id, T friendId);
    List<R> findAllFriends(T id);
    List<R> findCommonFriends(T id, T friendId);
}
