package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
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
public class FilmControllerTest {

    private final FilmController filmController;
    private final DirectorController directorController;

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

    private Film getTestFilm() {
        return Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.parse("1967-03-25", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)))
                .duration(10)
                .build();
    }

}
