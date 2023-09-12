package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorDbStorage extends AbstractStorage<Director> {
    void deleteById(long id);
}
