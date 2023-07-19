package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получение списка фильмов");
        return filmService.getAll();
    }

    @PutMapping
    public Film editFilm(@Valid @RequestBody Film film) throws ValidateException {
        log.info("Изменить фильм --> {}",  film);
        return filmService.update(film);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Создать фильм --> {}",  film);
        return filmService.create(film);
    }

}
