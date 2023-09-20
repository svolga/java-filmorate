package ru.yandex.practicum.filmorate.service.db;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.db.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class EventDbService {

    private final EventDbStorage eventDbStorage;
    private final UserDbStorage userDbStorage;

    public List<Event> findByUserId(long userId) {
        userDbStorage.findById(userId);
        return eventDbStorage.findByUserId(userId);
    }
}
