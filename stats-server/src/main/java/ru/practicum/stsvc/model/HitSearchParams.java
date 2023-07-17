package ru.practicum.stsvc.model;

import lombok.Getter;
import ru.practicum.stsvc.mapper.DateTimeMapper;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class HitSearchParams {

    private final LocalDateTime start;
    private final LocalDateTime end;
    private final List<String> uris;
    private final Boolean unique;

    public HitSearchParams(String start, String end, List<String> uris, Boolean unique) {
        this.start = DateTimeMapper.toDateTime(start);
        this.end = DateTimeMapper.toDateTime(end);
        this.uris = uris;
        this.unique = unique;
    }
}