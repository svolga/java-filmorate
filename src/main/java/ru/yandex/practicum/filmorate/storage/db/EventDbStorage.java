package ru.yandex.practicum.filmorate.storage.db;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.AbstractStorageCreator;
import ru.yandex.practicum.filmorate.storage.AbstractStorageGetter;

import java.util.List;

public interface EventDbStorage extends AbstractStorageCreator<Event>, AbstractStorageGetter<Event> {
    List<Event> findByUserId(long userId);
}
