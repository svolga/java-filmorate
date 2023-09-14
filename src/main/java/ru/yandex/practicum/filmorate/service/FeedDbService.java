package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class FeedDbService {

    private FeedDbStorage feedDbStorage;
    private UserDbStorage userDbStorage;

    public List<Feed> findByUserId(long userId){
        userDbStorage.findById(userId);
        return feedDbStorage.findByUserId(userId);
    }
}
