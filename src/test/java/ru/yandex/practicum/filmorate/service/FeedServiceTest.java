package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;
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

    private final EventDbStorage eventDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetAll() {
        jdbcTemplate.update("ALTER TABLE users " +
                "ALTER COLUMN user_id RESTART WITH 1");
        jdbcTemplate.update("DELETE FROM users");

        jdbcTemplate.update("ALTER TABLE films " +
                "ALTER COLUMN film_id RESTART WITH 1");
        jdbcTemplate.update("DELETE FROM films");

        jdbcTemplate.update("ALTER TABLE events " +
                "ALTER COLUMN event_id RESTART WITH 1");
        jdbcTemplate.update("DELETE FROM events ");
    }


    @Test
    void createFeed() {
        User user = getTestUser();
        Film film = getTestsFilm();

        user = userDbStorage.create(user);
        film = filmDbStorage.create(film);

        assertEquals(1, user.getId());
        assertEquals(1, film.getId());

        Event feed = eventDbStorage.create(
                Event.builder()
                        .userId(user.getId())
                        .entityId(film.getId())
                        .eventType(EventType.LIKE)
                        .operation(Operation.ADD)
                        .build()
        );

        assertEquals(1, feed.getEventId());

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
