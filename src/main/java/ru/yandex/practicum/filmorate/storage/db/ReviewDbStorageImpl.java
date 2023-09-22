package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@Primary
@AllArgsConstructor
public class ReviewDbStorageImpl implements ReviewDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final EventDbStorage eventDbStorage;

    @Override
    public Review createReview(Review review) {
        final String sqlQuery = "INSERT INTO reviews (film_id, user_id, content, is_positive, useful)" +
                "Values (?, ?, ?, ?, 0)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        if (jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setLong(1, review.getFilmId());
            stmt.setLong(2, review.getUserId());
            stmt.setString(3, review.getContent());
            stmt.setBoolean(4, review.getIsPositive());
            return stmt;
        }, keyHolder) > 0) {
            int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
            eventDbStorage.create(Event.builder().userId(review.getUserId()).entityId(id).eventType(EventType.REVIEW).operation(Operation.ADD).build());
            return findReviewById(id);
        }

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        Review oldReview = findReviewById(review.getReviewId());
        if (oldReview == null) {
            throw new ReviewNotFoundException("Review c id = " + review.getReviewId() + " не существует");
        }
        eventDbStorage.create(Event.builder().userId(oldReview.getUserId()).entityId(review.getReviewId()).eventType(EventType.REVIEW).operation(Operation.UPDATE).build());

        String sqlQuery = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findReviewById(review.getReviewId());
    }

    @Override
    public long removeReviewById(long id) {
        Review review = findReviewById(id);
        if (review == null) {
            throw new ReviewNotFoundException("Review c id = " + id + " не существует");
        }

        String sqlQuery = "DELETE FROM reviews WHERE review_id = ?";
        if (jdbcTemplate.update(sqlQuery, id) > 0) {
            eventDbStorage.create(Event.builder().userId(review.getUserId()).entityId(review.getReviewId()).eventType(EventType.REVIEW).operation(Operation.REMOVE).build());
        }
        return id;
    }

    @Override
    public Review findReviewById(long id) {
        String sqlQuery = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
        } catch (RuntimeException e) {
            log.info("Отзыв с идентификатором {} не найден.", id);
            throw new ReviewNotFoundException(String.valueOf(id));
        }
    }

    @Override
    public List<Review> findAllReviews(int filmId, int count) {
        String sqlQuery;

        StringBuilder subQuery = new StringBuilder();
        List<Object> parameters = new ArrayList<>();

        try {
            if (filmId > 0) {
                parameters.add(filmId);
                subQuery.append(" WHERE film_id = ? ");
            }
            parameters.add(count);
            sqlQuery = "SELECT * FROM reviews " + subQuery +
                    " ORDER BY useful DESC LIMIT ? ";

            return jdbcTemplate.query(sqlQuery, this::mapRowToReview, parameters.toArray());
        } catch (EmptyResultDataAccessException e) {
            log.info("У фильма с идентификатором {} нет отзывов.", filmId);
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
    public long removeLikeReview(int userId, long id) {
        String sql = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }

    @Override
    public long removeDislikeReview(int userId, long id) {
        String sql = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("review_id"))
                .filmId(resultSet.getLong("film_id"))
                .userId(resultSet.getLong("user_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .useful(resultSet.getLong("useful"))
                .build();
    }
}
