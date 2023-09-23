package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.db.ReviewDbService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Data
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewDbService reviewDbService;

    @Autowired
    public ReviewController(ReviewDbService reviewDbService) {
        this.reviewDbService = reviewDbService;
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        Review request = reviewDbService.createReview(review);
        log.debug("Добавление пользователем id = {} отзыва к фильму id = {}", review.getUserId(), review.getFilmId());
        return request;
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        Review request = reviewDbService.updateReview(review);
        log.debug("Правка пользователем id = {} отзыва id = {} к фильму id ={}", review.getUserId(),
                review.getReviewId(), review.getFilmId());
        return request;
    }

    @DeleteMapping("/{id}")
    public long deleteReviewById(@PathVariable long id) {
        long request = reviewDbService.removeReviewById(id);
        log.debug("Удаление отзыва id = {}", id);
        return request;
    }

    @GetMapping("/{id}")
    public Review findReviewById(@PathVariable long id) {
        Review request = reviewDbService.findReviewById(id);
        log.debug("Получение отзыва id = {}, к фильму id = {} от пользователя id = {}", id,
                request.getFilmId(), request.getUserId());
        return request;
    }

    @GetMapping
    public List<Review> findAllReviews(@RequestParam(defaultValue = "0") int filmId,
                                       @RequestParam(defaultValue = "10") int count) {
        List<Review> request = reviewDbService.findAllReviews(filmId, count);
        if (filmId == 0) {
            log.debug("Получение отзывов ко всем фильмам. В количестве не более {}", count);
        } else {
            log.debug("Получение отзывов к фильму id = {}. В количестве не более {}", filmId, count);
        }
        return request;
    }

    @PutMapping("{id}/like/{userId}")
    public void likeReview(@PathVariable long id, @PathVariable int userId) {
        reviewDbService.likeReview(userId, id);
        log.debug("Пользователь id = {} поставил лайк отзыву id = {}", userId, id);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void dislikeReview(@PathVariable long id, @PathVariable int userId) {
        reviewDbService.dislikeReview(userId, id);
        log.debug("Пользователь id = {} поставил дизлайк отзыву id = {}", userId, id);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLikeReview(@PathVariable long id, @PathVariable int userId) {
        reviewDbService.removeLikeReview(userId, id);
        log.debug("Пользователь id = {} удалил свой лайк отзыву id = {}", userId, id);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteDislikeReview(@PathVariable long id, @PathVariable int userId) {
        reviewDbService.removeDislikeReview(userId, id);
        log.debug("Пользователь id = {} удалил свой дизлайк отзыву id = {}", userId, id);
    }
}
