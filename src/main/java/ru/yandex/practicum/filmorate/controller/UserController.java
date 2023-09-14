package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDbService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserDbService userDbService;

    public UserController(UserService userService, UserDbService userDbService) {
        this.userService = userService;
        this.userDbService = userDbService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получение списка пользователей");
        return userDbService.getAll();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создать пользователя --> {}", user);
        return userDbService.create(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidateException {
        log.info("Изменить пользователя --> {}", user);
        return userDbService.update(user);
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable long id) {
        return userDbService.findUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Добавление для пользователя с id -->{} друга c friendId --> {}", id, friendId);
        userDbService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Удаление для пользователя с id -->{} друга c friendId --> {}", id, friendId);
        userDbService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findFriends(@PathVariable long id) {
        log.info("Поиск друзей для пользователя с id -->{}", id);
        return userDbService.findAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Поиск общих друзей для пользователя с id -->{} и пользователя с otherId --> {}", id, otherId);
        return userDbService.findCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> findRecommendations(@PathVariable long id) {
        log.info("Вывод рекомендаций для пользователя с id -->{}", id);
        return userDbService.findRecommendations(id);
    }

}
