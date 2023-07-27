package ru.practicum.stsvc.controller;

import ru.practicum.ewm.client.dto.BoxDto;
import ru.practicum.ewm.client.dto.HitPostDto;
import ru.practicum.ewm.client.dto.HitResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.stsvc.mapper.DateTimeMapper;
import ru.practicum.stsvc.model.Hit;
import ru.practicum.stsvc.service.StatsService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService ssService;

    @GetMapping("/views/{eventId}")
    public Long getViewsByEventId(@Positive @PathVariable Long eventId) {
        log.info("Запрос количества просмотров для события с id: {}", eventId);
        return ssService.getViewsByEventId(eventId);
    }

    @GetMapping("/views")
    public BoxDto getViewsByEventIds(@RequestParam List<String> ids) {
        log.info("Запрос количества просмотров для событий: {}", ids.toString());
        return ssService.getViewsByEventIds(ids);
    }

    @GetMapping("/stats")
    public List<HitResponseDto> getStatistics(@RequestParam @NotBlank String start,
                                              @RequestParam @NotBlank String end,
                                              @RequestParam(required = false) List<String> uris,
                                              @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Запрос статистики -- start:{}, end:{}, uris:{}, unique:{}", start, end, uris, unique);


        return ssService.getHits(DateTimeMapper.toDateTime(start), DateTimeMapper.toDateTime(end), uris, unique);
    }

    @PostMapping("/hit")
    public ResponseEntity<Hit> postHit(@Valid @RequestBody HitPostDto dto) {
        log.info("Запрос на сохранение запроса: {}", dto);
        return new ResponseEntity<>(ssService.postHit(dto), HttpStatus.CREATED);
    }
}
