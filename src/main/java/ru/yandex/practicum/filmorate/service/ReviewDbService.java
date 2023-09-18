package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;

import java.util.List;

@Service
@Data
public class ReviewDbService {

    private final ReviewDbStorage reviewDbStorage;

    @Autowired
    public ReviewDbService(ReviewDbStorage reviewDbStorage) {
        this.reviewDbStorage = reviewDbStorage;
    }

    public Review addReview(Review review) {
        if (review.getUserId() <= 0 || review.getFilmId() <= 0) {
            throw new ReviewNotFoundException(String.valueOf(review.getUserId()));
        }
        return reviewDbStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        return reviewDbStorage.updateReview(review);
    }

    public long deleteReviewById(Long id) {
        Review review = findReviewById(id);
        if (review.getUserId() <= 0) {
            throw new ReviewNotFoundException("Id пользователя не может быть равен 0 или быть отрицательным");
        }
        return reviewDbStorage.deleteReviewById(id);
    }

    public Review findReviewById(Long id) {
        return reviewDbStorage.findReviewById(id);
    }

    public List<Review> findAllReviews(int filmId, int count) {
        return reviewDbStorage.findAllReviews(filmId, count);
    }

    public long likeReview(int userId, long id) {
        return reviewDbStorage.likeReview(userId, id);
    }

    public long dislikeReview(int userId, long id) {
        return reviewDbStorage.dislikeReview(userId, id);
    }

    public long deleteLikeReview(int userId, long id) {
        return reviewDbStorage.deleteLikeReview(userId, id);
    }

    public long deleteDislikeReview(int userId, long id) {
        return reviewDbStorage.deleteDislikeReview(userId, id);
    }
}
