package ru.yandex.practicum.filmorate.exception;

public class ReviewFeedbackAlreadyExistsException extends RuntimeException {
    public ReviewFeedbackAlreadyExistsException(String message) {
        super(message);
    }
}
