package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import javax.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class Event {

    private long eventId;

    @NotNull
    private long userId;

    @NotNull
    private long entityId;

    @NotNull
    private EventType eventType;

    @NotNull
    private Operation operation;

    @JsonIgnore
    private LocalDateTime updatedAt;

    private long timestamp;

    public long getTimestamp() {
        return Timestamp.valueOf(updatedAt).getTime();
    }
}