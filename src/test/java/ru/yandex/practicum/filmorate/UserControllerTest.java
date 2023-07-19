package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
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
class UserControllerTest {

    @Autowired
    UserController userController;

    @Test
    void createUser() {
        User user = userController.addUser(getUserTest());
        assertTrue(user.getId() > 0);
    }

    @Test
    void updateUser() throws ValidateException {
        User user = userController.addUser(getUserTest());
        String newName = "demoname";
        user.setName(newName);
        user = userController.editUser(user);

        assertEquals(newName, user.getName());
    }

    @Test
    void getAllUsers() {
        List<User> users = userController.getAllUsers();
        userController.addUser(getUserTest());

        List<User> users2 = userController.getAllUsers();
        assertEquals(users2.size(), users.size() + 1);
    }

    @Test
    void shouldCheckUserFailedEmail() {
        User user = getUserTest();
        user.setEmail("mail.ru");

        ConstraintViolationException ex = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> userController.addUser(user)
        );
        assertEquals("addUser.user.email: Электронная почта должна содержать символ @", ex.getMessage());
    }

    @Test
    void shouldCheckUserEmptyLogin() {
        User user = getUserTest();
        user.setLogin("   ");
        ConstraintViolationException ex = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> userController.addUser(user)
        );
        assertEquals("addUser.user.login: Логин не может быть пустой", ex.getMessage());
    }

    @Test
    void shouldCheckUserEmptyName() {

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> User.builder()
                        .login("demodemo")
                        .email("mail@mail.ru")
                        .birthday(LocalDate.parse("1946-08-20", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)))
                        .build()
        );
        assertEquals("name is marked non-null but is null", ex.getMessage());
    }

    @Test
    void shouldCheckUserUpdateUnknown() {
        User user = getUserTest();
        user.setId(9999);

        ValidateException ex = Assertions.assertThrows(
                ValidateException.class,
                () -> userController.editUser(user)
        );
        assertEquals("Пользователь c id = 9999 не существует", ex.getMessage());
    }

    @Test
    void shouldCheckUserFailDateInFuture() {
        User user = getUserTest();
        user.setBirthday(LocalDate.parse("3946-08-20", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)));

        ConstraintViolationException ex = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> userController.addUser(user)
        );
        assertEquals("addUser.user.birthday: Дата рождения не может быть в будущем", ex.getMessage());
    }

    private User getUserTest(){
        return User.builder()
                .login("dolore1")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.parse("1946-08-20", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)))
                .build();
    }
}


