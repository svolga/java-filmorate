package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorControllerTest {

    private final DirectorController directorController;

    private final Director testDirector = Director.builder().name("Test Director").build();
    private final Director updatedDirector = Director.builder().name("Updated Director").build();

    @Test
    void shouldCreateDirector() {
        Director director = directorController.createDirector(testDirector);
        assertTrue(director.getId() > 0);
        assertEquals(director.getName(), testDirector.getName());
        directorController.deleteDirector(director.getId());
    }

    @Test
    void shouldGetAllDirectors() {
        Director director1 = directorController.createDirector(testDirector);
        Director director2 = directorController.createDirector(updatedDirector);
        List<Director> directors = directorController.getAllDirectors();
        assertEquals(directors.size(), 2);
        assertEquals(directors.get(0), director1);
        assertEquals(directors.get(1), director2);
        directorController.deleteDirector(director1.getId());
        directorController.deleteDirector(director2.getId());
    }


    @Test
    void shouldUpdateDirector() throws ValidateException {
        Director director = directorController.createDirector(testDirector);
        updatedDirector.setId(director.getId());
        director = directorController.updateDirector(updatedDirector);
        assertEquals(director.getName(), updatedDirector.getName());
        directorController.deleteDirector(director.getId());
    }

    @Test
    void shouldFindDirector() {
        Director director = directorController.createDirector(testDirector);
        Director foundDirector = directorController.findDirector(director.getId());
        assertEquals(foundDirector.getName(), testDirector.getName());
        directorController.deleteDirector(foundDirector.getId());
    }

    @Test
    void shouldDeleteDirector() {
        Director director = directorController.createDirector(testDirector);
        directorController.deleteDirector(director.getId());
        assertTrue(directorController.getAllDirectors().isEmpty());
    }
}