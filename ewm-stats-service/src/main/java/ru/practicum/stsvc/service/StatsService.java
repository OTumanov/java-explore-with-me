package ru.practicum.stsvc.service;


import ru.practicum.dto.BoxDto;
import ru.practicum.dto.HitPostDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.stsvc.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    Hit postHit(HitPostDto dto);

    List<HitResponseDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    Long getViewsByEventId(Long eventId);

    BoxDto getViewsByEventIds(List<String> eventIds);
}