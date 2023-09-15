package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedDbStorage extends AbstractStorageCreator<Feed>, AbstractStorageGetter<Feed> {
    List<Feed> findByUserId(long userId);
}
