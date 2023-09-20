package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements AbstractStorage<User> {

    private final Map<Long, User> users = new HashMap<>();
    private long id;

    private long getNextId() {
        return ++id;
    }

    @Override
    public User create(@Valid User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User update(@Valid User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return users.get(user.getId());
        }
        throw new UserNotFoundException("Пользователь c id = " + user.getId() + " не существует");
    }

    @Override
    public List<User> getAll() {
        return List.copyOf(users.values());
    }

    @Override
    public User findById(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException(String.format("User с id = %d не найден", id));
        }
        return user;
    }

}
