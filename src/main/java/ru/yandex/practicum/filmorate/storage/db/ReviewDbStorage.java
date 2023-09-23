package ru.yandex.practicum.filmorate.storage.db;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDbStorage {
    Review createReview(Review review);

    Review updateReview(Review review);

    long removeReviewById(long id);

    Review findReviewById(long id);

    List<Review> findAllReviews(int filmId, int count);

    void likeReview(int userId, long id);

    void dislikeReview(int userId, long id);

    void removeLikeReview(int userId, long id);

    void removeDislikeReview(int userId, long id);
}
