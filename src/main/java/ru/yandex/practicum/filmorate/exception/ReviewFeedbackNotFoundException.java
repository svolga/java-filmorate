package ru.yandex.practicum.filmorate.exception;

public class ReviewFeedbackNotFoundException extends RuntimeException {
    public ReviewFeedbackNotFoundException(String message) {
        super(message);
    }
}
