package ru.practicum.sd.utils;

import java.time.format.DateTimeFormatter;

public interface Constants {
    String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN);
}