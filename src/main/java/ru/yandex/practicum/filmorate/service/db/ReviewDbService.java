package ru.yandex.practicum.filmorate.service.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.List;

@Service
@AllArgsConstructor
@Data
public class ReviewDbService {

    private final ReviewDbStorage reviewDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    public Review createReview(Review review) {
        userDbStorage.findById(review.getUserId());
        filmDbStorage.findById(review.getFilmId());
        return reviewDbStorage.createReview(review);
    }

    public Review updateReview(Review review) {
        userDbStorage.findById(review.getUserId());
        filmDbStorage.findById(review.getFilmId());
        return reviewDbStorage.updateReview(review);
    }

    public long removeReviewById(Long id) {
        Review review = findReviewById(id);
        if (review == null) {
            throw new ReviewNotFoundException("Не найден review с id = " + id);
        }
        return reviewDbStorage.removeReviewById(id);
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

    public long removeLikeReview(int userId, long id) {
        return reviewDbStorage.removeLikeReview(userId, id);
    }

    public long removeDislikeReview(int userId, long id) {
        return reviewDbStorage.removeDislikeReview(userId, id);
    }
}
