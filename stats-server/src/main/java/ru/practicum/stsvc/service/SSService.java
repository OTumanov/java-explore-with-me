package ru.practicum.stsvc.service;


import ru.practicum.stsvc.dto.BoxDto;
import ru.practicum.stsvc.dto.HitPostDto;
import ru.practicum.stsvc.dto.HitResponseDto;
import ru.practicum.stsvc.model.HitSearchParams;

import java.util.List;

public interface SSService {
    void postHit(HitPostDto dto);

    List<HitResponseDto> getHits(HitSearchParams params);

    Long getViewsByEventId(Long eventId);

    BoxDto getViewsByEventIds(List<String> eventIds);
}