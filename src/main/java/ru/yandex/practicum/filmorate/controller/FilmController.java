package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDbService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {

    private final FilmDbService filmDbService;

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получение списка фильмов");
        return filmDbService.getAll();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Создать фильм --> {}", film);
        return filmDbService.create(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidateException {
        log.info("Изменить фильм --> {}", film);
        return filmDbService.update(film);
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable long id) {
        log.info("Поиск фильма с id  --> {}", id);
        return filmDbService.findFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Добавить like фильма с id  --> {}, userId --> {}", id, userId);
        filmDbService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Удалить like фильма с id  --> {}, userId --> {}", id, userId);
        filmDbService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findAllPopular(@RequestParam(value = "count", defaultValue = "10", required = false) int count) {
        log.info("Получить {} популярных фильмов", count);
        return filmDbService.findAllPopular(count);
    }

    @GetMapping("/search")
    public List<Film> findByTitleAndDirector(@RequestParam(value = "query", required = true) String query,
                                             @RequestParam(value = "by", required = true) String by) {
        log.info("Поиск -->{} фильма по названию и режисеру -->{} ", query, by);
        return filmDbService.findByTitleAndDirector(query, by);
    }

}
