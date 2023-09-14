package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Feed {

    private long id;

    @NotNull
    private long userId;

    @NotNull
    private long entityId;

    @NotNull
    private EventType eventType;

    @NotNull
    private Operation operation;

    private LocalDateTime updatedAt;

}