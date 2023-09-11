package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.AbstractStorageGetter;

import java.util.List;

@Slf4j
@Service
public class MpaDbService {

    private final AbstractStorageGetter<Mpa> mpaDbStorage;

    public MpaDbService(AbstractStorageGetter<Mpa> mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public List<Mpa> getAll() {
        return mpaDbStorage.getAll();
    }

    public Mpa findById(long id) {
        return mpaDbStorage.findById(id);
    }

}