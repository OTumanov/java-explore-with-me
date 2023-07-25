package ru.practicum.mnsvc.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.events.EventDetailedDto;
import ru.practicum.mnsvc.dto.events.EventPostDto;
import ru.practicum.mnsvc.model.EventSearchParams;
import ru.practicum.mnsvc.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    public List<EventDetailedDto> findEventsByConditions(@RequestParam(required = false) List<Long> userIds,
                                                         @RequestParam(required = false) List<String> states,
                                                         @RequestParam(required = false) List<Long> categories,
                                                         @RequestParam(required = false) String rangeStart,
                                                         @RequestParam(required = false) String rangeEnd,
                                                         @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                                         @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        EventSearchParams searchParams = new EventSearchParams(
                userIds,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size
        );
        log.info("find event by conditions {}", searchParams);
        return eventService.findEventsByConditions(searchParams);
    }

    @PutMapping("/{eventId}")
    public EventDetailedDto editEvent(@Positive @PathVariable Long eventId,
                                      @RequestBody EventPostDto dto) {
        log.info("Редактировать событие id:{}, {}", eventId, dto);
        return eventService.editEvent(eventId, dto);
    }

    @PatchMapping("/{eventId}")
    public EventDetailedDto publishEvent(@Positive @PathVariable Long eventId,
                                        @RequestBody EventPostDto dto) {
        log.info("Опубликовать событие id: {}. Получен статус: {}", eventId, dto.getStateAction());
        return eventService.publishEvent(eventId, dto);
    }
}