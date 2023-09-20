package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDbStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    long deleteReviewById(long id);

    Review findReviewById(long id);

    List<Review> findAllReviews(int filmId, int count);

    long likeReview(int userId, long id);

    long dislikeReview(int userId, long id);

    long deleteLikeReview(int userId, long id);

    long deleteDislikeReview(int userId, long id);
}
