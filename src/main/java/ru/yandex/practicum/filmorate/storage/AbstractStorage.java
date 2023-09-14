package ru.yandex.practicum.filmorate.storage;

public interface AbstractStorage<T> extends AbstractStorageCreator<T>, AbstractStorageUpdater<T>, AbstractStorageGetter<T> {

}
