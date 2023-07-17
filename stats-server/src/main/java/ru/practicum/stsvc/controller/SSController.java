package ru.practicum.stsvc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stsvc.dto.BoxDto;
import ru.practicum.stsvc.dto.HitPostDto;
import ru.practicum.stsvc.dto.HitResponseDto;
import ru.practicum.stsvc.mapper.DateTimeMapper;
import ru.practicum.stsvc.model.Hit;
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
                                              @RequestParam List<String> uris, @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Запрос статистики -- start:{}, end:{}, uris:{}, unique:{}", start, end, uris, unique);
        HitSearchParams params = HitSearchParams.builder()
                .start(DateTimeMapper.toDateTime(start))
                .end(DateTimeMapper.toDateTime(end))
                .uris(uris)
                .unique(unique)
                .build();
        return ssService.getHits(params);
    }

    @PostMapping("/hit")
    public ResponseEntity<Hit> postHit(@Valid @RequestBody HitPostDto dto) {
        log.info("Сохранение запроса: {}", dto);
        return new ResponseEntity<>(ssService.postHit(dto), HttpStatus.CREATED);
    }
}
