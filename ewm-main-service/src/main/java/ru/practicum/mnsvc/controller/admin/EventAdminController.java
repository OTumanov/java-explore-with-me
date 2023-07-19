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

    public static final String DEFAULT_FROM = "0";
    public static final String DEFAULT_SIZE = "10";

    private final EventService eventService;

    @GetMapping
    public List<EventDetailedDto> findEventsByConditions(@RequestParam(required = false) List<Long> userIds,
                                                         @RequestParam(required = false) List<String> states,
                                                         @RequestParam(required = false) List<Long> categories,
                                                         @RequestParam(required = false) String rangeStart,
                                                         @RequestParam(required = false) String rangeEnd,
                                                         @PositiveOrZero
                                                         @RequestParam(required = false, defaultValue = DEFAULT_FROM)
                                                         Integer from,
                                                         @Positive
                                                         @RequestParam(required = false, defaultValue = DEFAULT_SIZE)
                                                         Integer size) {
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
    public EventDetailedDto editEvent(@Positive
                                      @PathVariable Long eventId,
                                      @RequestBody EventPostDto dto) {
        log.info("edit event id:{}, {}", eventId, dto);
        return eventService.editEvent(eventId, dto);
    }

    @PatchMapping("/{eventId}/publish")
    public EventDetailedDto publishEvent(@Positive
                                         @PathVariable Long eventId) {
        log.info("publish event id: {}", eventId);
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventDetailedDto rejectEvent(@Positive
                                        @PathVariable Long eventId) {
        log.info("reject event id: {}", eventId);
        return eventService.rejectEvent(eventId);
    }
}