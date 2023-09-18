package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class ReviewDbStorageImpl implements ReviewDbStorage {

    @Autowired
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Override
    public Review addReview(Review review) {
        final String sql = "INSERT INTO reviews (film_id, user_id, content, is_positive, useful)" +
                "Values (?, ?, ?, ?, 0)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"review_id"});
            stmt.setInt(1, review.getFilmId());
            stmt.setInt(2, review.getUserId());
            stmt.setString(3, review.getContent());
            stmt.setBoolean(4, review.getIsPositive());
            return stmt;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        review.setReviewId(id);
        review.setUseful(0L);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        findReviewById(review.getReviewId());
        String sql = "UPDATE reviews SET content = ?," +
                "is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return jdbcTemplate.queryForObject("SELECT * FROM reviews WHERE review_id = ?",
                this::mapRowToReview, review.getReviewId());
    }

    @Override
    public long deleteReviewById(long id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }

    @Override
    public Review findReviewById(long id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToReview, id);
        } catch (RuntimeException e) {
            log.info("Отзыв с индификатором {} не найден.", id);
            throw new ReviewNotFoundException(String.valueOf(id));
        }
    }

    @Override
    public List<Review> findAllReviews(int filmId, int count) {
        List<Review> allReviews = new ArrayList<>();
        String sql;
        if (filmId == 0) {
            sql = "SELECT * FROM reviews " +
                    "ORDER BY useful desc LIMIT ?";
            allReviews.addAll(jdbcTemplate.query(sql, this::mapRowToReview, count));
            return allReviews;
        }
        sql = "SELECT * FROM reviews WHERE film_id = ? " +
                "ORDER BY useful desc LIMIT?";
        try {
            return jdbcTemplate.query(sql, this::mapRowToReview, filmId, count);
        } catch (EmptyResultDataAccessException e) {
            log.info("У фильма с индификатором {} нет отзывов.", filmId);
            throw new ReviewNotFoundException("Отзыв не найден");
        }
    }

    @Override
    public long likeReview(int userId, long id) {
        String sql = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }

    @Override
    public long dislikeReview(int userId, long id) {
        String sql = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }

    @Override
    public long deleteLikeReview(int userId, long id) {
        String sql = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }

    @Override
    public long deleteDislikeReview(int userId, long id) {
        String sql = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("review_id"))
                .filmId(resultSet.getInt("film_id"))
                .userId(resultSet.getInt("user_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .useful(resultSet.getLong("useful"))
                .build();
    }
}
