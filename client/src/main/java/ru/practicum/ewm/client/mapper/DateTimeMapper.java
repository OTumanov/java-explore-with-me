package ru.practicum.ewm.client.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeMapper {

    private DateTimeMapper() {
    }

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static DateTimeFormatter formatter;

    public static String toString(LocalDateTime date) {
        formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        return date.format(formatter);
    }
}