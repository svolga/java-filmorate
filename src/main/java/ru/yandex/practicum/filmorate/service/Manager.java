package ru.yandex.practicum.filmorate.service;

import java.util.List;

public interface Manager<T>{

    T create (T t);
    T update (T t);
    List<T> getAll();
}
