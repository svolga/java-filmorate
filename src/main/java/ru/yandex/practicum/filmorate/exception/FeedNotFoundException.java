package ru.yandex.practicum.filmorate.exception;

public class FeedNotFoundException extends RuntimeException {
    public FeedNotFoundException(String message) {
        super(message);
    }
}
