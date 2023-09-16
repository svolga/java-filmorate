package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.util.Const;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FeedServiceTest {

    private final FeedDbStorage feedDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Test
    void createFeed() {
        User user = getTestUser();
        Film film = getTestsFilm();

        user = userDbStorage.create(user);
        film = filmDbStorage.create(film);

        assertEquals(1, user.getId());
        assertEquals(1, film.getId());

        Feed feed = feedDbStorage.create(
                Feed.builder()
                        .userId(user.getId())
                        .entityId(film.getId())
                        .eventType(EventType.LIKE)
                        .operation(Operation.ADD)
                        .build()
        );

        assertEquals(1, feed.getId());

        System.out.println("feed = " + feed);
    }

    private User getTestUser() {
        return User.builder()
                .name("demo_name")
                .email("1@1.com")
                .login("demo_login")
                .birthday(LocalDate.parse("2000-01-30", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)))
                .build();
    }

    private Film getTestsFilm() {
        return Film.builder()
                .name("Тестовый фильм")
                .rate(8.9)
                .releaseDate(LocalDate.parse("1967-03-25", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)))
                .duration(100)
                .build();
    }


}
