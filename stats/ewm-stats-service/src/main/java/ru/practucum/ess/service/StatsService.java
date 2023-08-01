package ru.practucum.ess.service;


import ru.practicum.sd.dto.EndpointHitDto;
import ru.practicum.sd.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    EndpointHitDto createEndpointHit(EndpointHitDto endpointHitDto);

}