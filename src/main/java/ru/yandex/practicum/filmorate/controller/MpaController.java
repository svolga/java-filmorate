package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.db.MpaDbService;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/mpa")
public class MpaController {

    private final MpaDbService mpaDbService;

    public MpaController(MpaDbService mpaDbService) {
        this.mpaDbService = mpaDbService;
    }

    @GetMapping
    public List<Mpa> getAll() {
        log.info("Получение списка рейтингов");
        return mpaDbService.getAll();
    }

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable long id) {
        return mpaDbService.findById(id);
    }

}
