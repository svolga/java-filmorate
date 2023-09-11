package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractStorageGetter;

import java.util.List;

@Slf4j
@Service
public class GenreDbService {

    private final AbstractStorageGetter<Genre> genreDbStorage;

    public GenreDbService(AbstractStorageGetter<Genre> genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public List<Genre> getAll() {
        return genreDbStorage.getAll();
    }

    public Genre findById(long id) {
        return genreDbStorage.findById(id);
    }

}
