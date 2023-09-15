package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.Const;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {

    private final FilmController filmController;
    private final DirectorController directorController;
    private final UserController userController;

    @Test
    void createFilm() {
        Film film = filmController.createFilm(getTestFilm());
        assertTrue(film.getId() > 0);
    }

    @Test
    void updateFilm() throws ValidateException {
        Film film = filmController.createFilm(getTestFilm());

        String newName = "demoname";
        film.setName(newName);
        film = filmController.updateFilm(film);

        assertEquals(newName, film.getName());
    }

    @Test
    void shouldGetAllFilms() {
        List<Film> films = filmController.getAllFilms();
        filmController.createFilm(getTestFilm());
        List<Film> films2 = filmController.getAllFilms();
        assertEquals(films2.size(), films.size() + 1);
    }

    @Test
    void shouldCheckFilmDuration() {
        Film film = getTestFilm();
        film.setDuration(-1);

        ConstraintViolationException ex = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> filmController.createFilm(film)
        );
        assertEquals("createFilm.film.duration: Продолжительность фильма должна быть положительной", ex.getMessage());
    }

    @Test
    void shouldCheckFilmFailReleaseDate() {

        Film film = getTestFilm();
        film.setReleaseDate(LocalDate.parse("1667-03-25", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)));

        ConstraintViolationException ex = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> filmController.createFilm(film)
        );
        assertEquals("createFilm.film.releaseDate: Дата должна быть больше 1895-12-28", ex.getMessage());
    }

    @Test
    void shouldCheckFilmFailDescription() {
        Film film = getTestFilm();
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят " +
                "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                "который за время «своего отсутствия», стал кандидатом Коломбани.");

        ConstraintViolationException ex = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> filmController.createFilm(film)
        );
        assertEquals("createFilm.film.description: Максимальная длина описания — 200 символов", ex.getMessage());
    }

    @Test
    void shouldCheckFilmEmptyName() {

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> Film.builder()
                        .description("adipisicing")
                        .releaseDate(LocalDate.parse("1967-03-25", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)))
                        .duration(10)
                        .build()
        );

        assertEquals("name is marked non-null but is null", ex.getMessage());
    }

    @Test
    void shouldCheckFilmUpdateUnknown() {
        Film film = getTestFilm();
        film.setId(9999);

        FilmNotFoundException ex = Assertions.assertThrows(
                FilmNotFoundException.class,
                () -> filmController.updateFilm(film)
        );
        assertEquals("Фильм с id = 9999 не найден", ex.getMessage());
    }

    @Test
    void shouldFindFilm() {
        Film film = getTestFilm();
        filmController.createFilm(film);

        List<Film> films = filmController.findByTitleAndDirector("SMOD", "title");

        assertEquals(1, films.size());
    }

    @Test
    void shouldFindDirectorsFilm() {
        Director director = new Director(1, "Director");
        director = directorController.createDirector(director);
        Film film1 = getTestFilm();
        film1.getDirectors().add(director);
        Film film2 = getTestFilm();
        film2.getDirectors().add(director);
        film2.setReleaseDate(film1.getReleaseDate().minusDays(1));

        film1 = filmController.createFilm(film1);
        film2 = filmController.createFilm(film2);
        List<Film> films = filmController.findDirectorsFilms(director.getId(), "year");

        assertEquals(2, films.size());
        assertEquals(films.get(0), film2);
        assertEquals(films.get(1), film1);
        assertEquals(2, films.size());
    }

    @Test
    void shouldFindAllPopularFilms() {
        List<Long> filmsToDelete = filmController.getAllFilms().stream().map(Film::getId).collect(Collectors.toList());
        for (Long id : filmsToDelete) {
            filmController.removeFilmById(id);
        }

        Director director1 = new Director(1, "Director 1");
        director1 = directorController.createDirector(director1);

        Director director2 = new Director(1, "Director 2");
        director2 = directorController.createDirector(director2);

        Film film1 = getTestFilm();
        film1.getDirectors().add(director1);
        film1.getGenres().add(new Genre(1, null));

        Film film2 = getTestFilm();
        film2.getDirectors().add(director1);
        film2.setReleaseDate(film1.getReleaseDate());
        film2.getGenres().add(new Genre(2, null));

        Film film3 = getTestFilm();
        film3.getDirectors().add(director2);
        film3.setReleaseDate(film1.getReleaseDate().plusYears(1));
        film3.getGenres().add(new Genre(2, null));

        film1 = filmController.createFilm(film1);
        film2 = filmController.createFilm(film2);
        film3 = filmController.createFilm(film3);

        User user1 = userController.createUser(getTestUser());
        User user2 = userController.createUser(getTestUser());

        filmController.addLike(film1.getId(), user1.getId());
        filmController.addLike(film2.getId(), user1.getId());
        filmController.addLike(film2.getId(), user2.getId());

        film1 = filmController.findFilm(film1.getId());
        film2 = filmController.findFilm(film2.getId());
        film3 = filmController.findFilm(film3.getId());

        List<Film> films = filmController.findAllPopular(10, 2L, null);
        assertEquals(2, films.size(),
                "Неправильный размер массива фильмов [count 10, genreId 2, year null]");
        assertEquals(film2, films.get(0),
                "Неправильный фильм в первой ячейке массива [count 10, genreId 2, year null]");
        assertEquals(film3, films.get(1),
                "Неправильный фильм во второй ячейке массива [count 10, genreId 2, year null]");

        films = filmController.findAllPopular(1, 2L, null);
        assertEquals(1, films.size(),
                "Неправильный размер массива фильмов [count 1, genreId 2, year null]");

        films = filmController.findAllPopular(10, null, 1967);
        assertEquals(2, films.size(),
                "Неправильный размер массива фильмов [count 10, genreId null, year 1967]");
        assertEquals(film2, films.get(0),
                "Неправильный фильм в первой ячейке массива [count 10, genreId null, year 1967]");
        assertEquals(film1, films.get(1),
                "Неправильный фильм во второй ячейке массива [count 10, genreId null, year 1967]");

        films = filmController.findAllPopular(10, 2L, 1967);
        assertEquals(1, films.size(),
                "Неправильный размер массива фильмов [count 10, genreId 2, year 1967]");
        assertEquals(film2, films.get(0),
                "Неправильный фильм в первой ячейке массива [count 10, genreId 2, year 1967]");
    }

    private Film getTestFilm() {
        return Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.parse("1967-03-25", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)))
                .duration(10)
                .build();
    }

    private User getTestUser() {
        return User.builder()
                .login("Login")
                .name("Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.parse("1946-08-20", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)))
                .build();
    }
}
