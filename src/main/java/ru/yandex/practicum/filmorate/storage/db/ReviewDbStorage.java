package ru.yandex.practicum.filmorate.storage.db;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDbStorage {
    Review createReview(Review review);

    Review updateReview(Review review);

    long removeReviewById(long id);

    Review findReviewById(long id);

    List<Review> findAllReviews(int filmId, int count);

    long likeReview(int userId, long id);

    long dislikeReview(int userId, long id);

    long removeLikeReview(int userId, long id);

    long removeDislikeReview(int userId, long id);
}
