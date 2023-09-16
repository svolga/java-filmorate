package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.Const;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    private final UserController userController;

    @Test
    void createUser() {
        User user = userController.createUser(getUserTest());
        assertTrue(user.getId() > 0);
    }

    @Test
    void createFriend() {
        User user = userController.createUser(getUserTest());
        User user2 = userController.createUser(getUserTest());
        userController.addFriend(user.getId(), user2.getId());
        List<User> friends = userController.findFriends(user.getId());

        assertEquals(1, friends.size());
    }

    @Test
    void updateUser() throws ValidateException {
        User user = userController.createUser(getUserTest());
        String newName = "demoname";
        user.setName(newName);
        user = userController.updateUser(user);

        assertEquals(newName, user.getName());
    }

    @Test
    void getAllUsers() {
        List<User> users = userController.getAllUsers();
        userController.createUser(getUserTest());

        List<User> users2 = userController.getAllUsers();
        assertEquals(users2.size(), users.size() + 1);
    }

    @Test
    void shouldCheckUserFailedEmail() {
        User user = getUserTest();
        user.setEmail("mail.ru");

        ConstraintViolationException ex = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> userController.createUser(user)
        );
        assertEquals("createUser.user.email: Электронная почта должна содержать символ @", ex.getMessage());
    }

    @Test
    void shouldCheckUserEmptyLogin() {
        User user = getUserTest();
        user.setLogin("   ");
        ConstraintViolationException ex = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> userController.createUser(user)
        );
        assertEquals("createUser.user.login: Логин не может быть пустой", ex.getMessage());
    }

    @Test
    void shouldCheckUserEmptyName() {

        User user = getUserTest();
        user.setName(null);
        User user2 = userController.createUser(user);

        assertEquals(user.getLogin(), user2.getName());
    }

    @Test
    void shouldCheckUserUpdateUnknown() {
        User user = getUserTest();
        user.setId(9999);

        UserNotFoundException ex = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userController.updateUser(user)
        );
        assertEquals("User с id = 9999 не найден", ex.getMessage());
    }

    @Test
    void shouldCheckUserFailDateInFuture() {
        User user = getUserTest();
        user.setBirthday(LocalDate.parse("3946-08-20", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)));

        ConstraintViolationException ex = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> userController.createUser(user)
        );
        assertEquals("createUser.user.birthday: Дата рождения не может быть в будущем", ex.getMessage());
    }

    private User getUserTest() {
        return User.builder()
                .login("dolore1")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.parse("1946-08-20", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)))
                .build();
    }
}


