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
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorDbService;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@Slf4j
@Validated
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorDbService directorDbService;

    @GetMapping
    public List<Director> getAllDirectors() {
        log.info("Получение списка режжисёров");
        return directorDbService.getAll();
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Создать режжисёра --> {}", director);
        return directorDbService.create(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) throws ValidateException {
        log.info("Изменить режжисёра --> {}", director);
        return directorDbService.update(director);
    }

    @GetMapping("/{id}")
    public Director findDirector(@PathVariable long id) {
        log.info("Поиск режжисёра с id  --> {}", id);
        return directorDbService.findDirectorById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable long id) {
        log.info("Поиск режжисёра с id  --> {}", id);
        directorDbService.deleteDirectorById(id);
    }
}
