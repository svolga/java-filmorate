package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

@Service
public class UserService implements Manager<User> {

    private final Map<Integer, User> users = new HashMap<>();
    private int id;

    private int getNextId() {
        return ++id;
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User update(User user) throws ValidateException {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return users.get(user.getId());
        }
        throw new ValidateException("Пользователь c id = " + user.getId() +" не существует");
    }

    @Override
    public List<User> getAll() {
        return List.copyOf(users.values());
    }
}
