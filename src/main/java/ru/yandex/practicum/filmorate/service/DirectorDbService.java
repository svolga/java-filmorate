package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DirectorDbService {

    private final DirectorDbStorage directorDbStorage;

    public Director create(Director director) {
        return directorDbStorage.create(director);
    }

    public Director update(Director director) throws ValidateException {
        return directorDbStorage.update(director);
    }

    public List<Director> getAll() {
        return directorDbStorage.getAll();
    }

    public Director findDirectorById(long id) {
        return directorDbStorage.findById(id);
    }

    public void deleteDirectorById(long id) {
        directorDbStorage.deleteById(id);
    }
}
