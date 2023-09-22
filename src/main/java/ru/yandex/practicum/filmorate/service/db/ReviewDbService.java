package ru.yandex.practicum.filmorate.service.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
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
        findReviewById(review.getReviewId());
        userDbStorage.findById(review.getUserId());
        filmDbStorage.findById(review.getFilmId());
        return reviewDbStorage.updateReview(review);
    }

    public long removeReviewById(Long id) {
        findReviewById(id);
        return reviewDbStorage.removeReviewById(id);
    }

    public Review findReviewById(Long id) {
        return reviewDbStorage.findReviewById(id);
    }

    public List<Review> findAllReviews(int filmId, int count) {
        if (filmId != 0) {
            filmDbStorage.findById(filmId);
        }
        return reviewDbStorage.findAllReviews(filmId, count);
    }

    public void likeReview(int userId, long id) {
        userDbStorage.findById(userId);
        findReviewById(id);
        reviewDbStorage.likeReview(userId, id);
    }

    public void dislikeReview(int userId, long id) {
        userDbStorage.findById(userId);
        findReviewById(id);
        reviewDbStorage.dislikeReview(userId, id);
    }

    public void removeLikeReview(int userId, long id) {
        userDbStorage.findById(userId);
        findReviewById(id);
        reviewDbStorage.removeLikeReview(userId, id);
    }

    public void removeDislikeReview(int userId, long id) {
        userDbStorage.findById(userId);
        findReviewById(id);
        reviewDbStorage.removeDislikeReview(userId, id);
    }
}
