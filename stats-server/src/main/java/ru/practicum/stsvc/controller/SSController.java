package ru.practicum.stsvc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stsvc.dto.BoxDto;
import ru.practicum.stsvc.dto.HitPostDto;
import ru.practicum.stsvc.dto.HitResponseDto;
import ru.practicum.stsvc.model.HitSearchParams;
import ru.practicum.stsvc.service.SSService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class SSController {

    private final SSService ssService;

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
    public List<HitResponseDto> getStatistics(@RequestParam @NotBlank String start, @RequestParam @NotBlank String end,
                                              @RequestParam List<String> uris, @RequestParam Boolean unique) {
        log.info("Запрос статистики -- start:{}, end:{}, uris:{}, unique:{}", start, end, uris, unique);
        HitSearchParams params = new HitSearchParams(start, end, uris, unique);
        return ssService.getHits(params);
    }

    @PostMapping("/hit")
    public void postHit(@Valid @RequestBody HitPostDto dto) {
        log.info("Сохранение запроса: {}", dto);
        ssService.postHit(dto);
    }
}
