package ru.practicum.stsvc.mapper;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
public final class DateTimeMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime toDateTime(String str) {
        return LocalDateTime.parse(str, formatter);
    }

    public static String toString(LocalDateTime date) {
        return date.format(formatter);
    }
}