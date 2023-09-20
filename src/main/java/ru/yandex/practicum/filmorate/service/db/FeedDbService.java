package ru.yandex.practicum.filmorate.service.db;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.db.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class FeedDbService {

    private final EventDbStorage feedDbStorage;
    private final UserDbStorage userDbStorage;

    public List<Event> findByUserId(long userId) {
        userDbStorage.findById(userId);
        return feedDbStorage.findByUserId(userId);
    }
}
