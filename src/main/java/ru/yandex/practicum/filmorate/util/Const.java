package ru.yandex.practicum.filmorate.util;

import java.util.Set;

public class Const {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String MIN_FILM_DATE = "1895-12-28";

    public static final String DIRECTOR_SEARCH = "director";
    public static final String TITLE_SEARCH = "title";

    public static final Set<String> SEARCH_FILM = Set.of(DIRECTOR_SEARCH, TITLE_SEARCH);

}
