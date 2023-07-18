package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.ValidateException;

import java.util.List;

public interface Manager<T> {

    T create(T t);

    T update(T t) throws ValidateException;

    List<T> getAll();
    
}
