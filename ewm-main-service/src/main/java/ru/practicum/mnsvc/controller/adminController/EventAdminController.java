package ru.practicum.mnsvc.controller.adminController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.events.EventDetailedDto;
import ru.practicum.mnsvc.dto.events.EventPostDto;
import ru.practicum.mnsvc.model.EventSearchParams;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    public static final String DEFAULT_FROM = "0";
    public static final String DEFAULT_SIZE = "10";

    private final EventService eventService;

    @GetMapping
    public List<EventDetailedDto> findEventsByConditions(@RequestParam List<Long> userIds,
                                                         @RequestParam List<String> states,
                                                         @RequestParam List<Long> categories,
                                                         @RequestParam String rangeStart,
                                                         @RequestParam String rangeEnd,
                                                         @RequestParam(defaultValue = DEFAULT_FROM) Integer from,
                                                         @RequestParam(defaultValue = DEFAULT_SIZE) Integer size) {
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
    public EventDetailedDto editEvent(@PathVariable Long eventId,
                                      @RequestBody EventPostDto dto) {
        log.info("edit event id:{}, {}", eventId, dto);
        return eventService.editEvent(eventId, dto);
    }

    @PatchMapping("/{eventId}/publish")
    public EventDetailedDto publishEvent(@PathVariable Long eventId) {
        log.info("publish event id: {}", eventId);
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventDetailedDto rejectEvent(@PathVariable Long eventId) {
        log.info("reject event id: {}", eventId);
        return eventService.rejectEvent(eventId);
    }
}