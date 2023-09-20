package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.db.GenreDbService;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/genres")
public class GenreController {

    private final GenreDbService genreDbService;

    public GenreController(GenreDbService genreDbService) {
        this.genreDbService = genreDbService;
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("Получение списка жанров");
        return genreDbService.getAll();
    }

    @GetMapping("/{id}")
    public Genre findGenre(@PathVariable long id) {
        return genreDbService.findById(id);
    }
}
