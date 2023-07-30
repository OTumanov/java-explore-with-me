package ru.practucum.ess.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.sd.dto.EndpointHitDto;
import ru.practicum.sd.dto.ViewStatsDto;
import ru.practicum.sd.utils.Constants;
import ru.practucum.ess.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto createEndpointHit(@RequestBody @Validated EndpointHitDto endpointHitDto) {
        log.info("StatsController - POST: /hit endpointHitDto={}", endpointHitDto);
        return statsService.createEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getViewStats(
            @RequestParam @DateTimeFormat(pattern = Constants.DATE_TIME_PATTERN) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = Constants.DATE_TIME_PATTERN) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("StatsController - GET: /stats start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);
        return statsService.getViewStats(start, end, uris, unique);
    }
}
