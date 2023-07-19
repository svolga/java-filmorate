package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.Const;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FilmControllerTest {

    @Autowired
    FilmController filmController;

    private Film getTestFilm() {
        return Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.parse("1967-03-25", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)))
                .duration(10)
                .build();
    }

    @Test
    void createFilm() {
        Film film = filmController.addFilm(getTestFilm());
        assertTrue(film.getId() > 0);
    }

    @Test
    void updateFilm() throws ValidateException {
        Film film = filmController.addFilm(getTestFilm());

        String newName = "demoname";
        film.setName(newName);
        film = filmController.editFilm(film);

        assertEquals(newName, film.getName());
    }

    @Test
    void shouldGetAllFilms() {
        List<Film> films = filmController.getAllFilms();
        filmController.addFilm(getTestFilm());
        List<Film> films2 = filmController.getAllFilms();
        assertEquals(films2.size(), films.size() + 1);
    }

    @Test
    void shouldCheckFilmDuration() {
        Film film = getTestFilm();
        film.setDuration(-1);

        ConstraintViolationException ex = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> filmController.addFilm(film)
        );
        assertEquals("addFilm.film.duration: Продолжительность фильма должна быть положительной", ex.getMessage());
    }

    @Test
    void shouldCheckFilmFailReleaseDate() {

        Film film = getTestFilm();
        film.setReleaseDate(LocalDate.parse("1667-03-25", DateTimeFormatter.ofPattern(Const.DATE_FORMAT)));

        ConstraintViolationException ex = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> filmController.addFilm(film)
        );
        assertEquals("addFilm.film.releaseDate: Дата должна быть больше 1895-12-28", ex.getMessage());
    }

    @Test
    void shouldCheckFilmFailDescription() {
        Film film = getTestFilm();
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят " +
                "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                "который за время «своего отсутствия», стал кандидатом Коломбани.");

        ConstraintViolationException ex = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> filmController.addFilm(film)
        );
        assertEquals("addFilm.film.description: Максимальная длина описания — 200 символов", ex.getMessage());
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

        ValidateException ex = Assertions.assertThrows(
                ValidateException.class,
                () -> filmController.editFilm(film)
        );
        assertEquals("Фильм с id = 9999 не существует", ex.getMessage());
    }


}