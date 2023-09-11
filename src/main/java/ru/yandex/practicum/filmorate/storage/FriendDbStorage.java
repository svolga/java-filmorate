package ru.yandex.practicum.filmorate.storage;

public interface FriendDbStorage {

    boolean createFriend(long id, long friendId);

    boolean removeFriend(long id, long friendId);

    boolean isHasFriend(long id, long friendId);
}

