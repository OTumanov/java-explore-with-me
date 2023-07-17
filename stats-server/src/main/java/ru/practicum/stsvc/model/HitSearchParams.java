package ru.practicum.stsvc.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class HitSearchParams {

    private final LocalDateTime start;
    private final LocalDateTime end;
    private final List<String> uris;
    private final Boolean unique;
}