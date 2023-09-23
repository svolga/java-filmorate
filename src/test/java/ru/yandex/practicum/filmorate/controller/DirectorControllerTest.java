package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorControllerTest {

    private final DirectorController directorController;

    private final Director testDirector = Director.builder().name("Test Director").build();
    private final Director updatedDirector = Director.builder().name("Updated Director").build();

    @BeforeEach
    void deleteAllDirectors() {
        List<Long> directorsId = directorController.getAllDirectors().stream()
                .map(Director::getId)
                .collect(Collectors.toList());
        for (long id : directorsId) {
            directorController.deleteDirector(id);
        }
    }

    @Test
    void shouldCreateDirector() {
        Director director = directorController.createDirector(testDirector);
        assertTrue(director.getId() > 0);
        assertEquals(director.getName(), testDirector.getName());
    }

    @Test
    void shouldGetAllDirectors() {
        Director director1 = directorController.createDirector(testDirector);
        Director director2 = directorController.createDirector(updatedDirector);
        List<Director> directors = directorController.getAllDirectors();
        assertEquals(directors.size(), 2);
        assertEquals(directors.get(0), director1);
        assertEquals(directors.get(1), director2);
    }


    @Test
    void shouldUpdateDirector() throws ValidateException {
        Director director = directorController.createDirector(testDirector);
        updatedDirector.setId(director.getId());
        director = directorController.updateDirector(updatedDirector);
        assertEquals(director.getName(), updatedDirector.getName());
    }

    @Test
    void shouldFindDirector() {
        Director director = directorController.createDirector(testDirector);
        Director foundDirector = directorController.findDirector(director.getId());
        assertEquals(foundDirector.getName(), testDirector.getName());
    }

    @Test
    void shouldDeleteDirector() {
        Director director = directorController.createDirector(testDirector);
        directorController.deleteDirector(director.getId());
        assertTrue(directorController.getAllDirectors().isEmpty());
    }
}