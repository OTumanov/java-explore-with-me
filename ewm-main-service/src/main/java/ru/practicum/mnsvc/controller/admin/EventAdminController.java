package ru.practicum.mnsvc.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.events.EventDetailedDto;
import ru.practicum.mnsvc.dto.events.EventPostDto;
import ru.practicum.mnsvc.model.EventSearchParams;
import ru.practicum.mnsvc.service.EventService;


import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    public List<EventDetailedDto> findEventsByConditions(@RequestParam List<Long> userIds,
                                                         @RequestParam List<String> states,
                                                         @RequestParam List<Long> categories,
                                                         @RequestParam String rangeStart,
                                                         @RequestParam String rangeEnd,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        EventSearchParams searchParams = new EventSearchParams(
                userIds,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size
        );
        log.info("Получить события по условиям: {}", searchParams);
        return eventService.findEventsByConditions(searchParams);
    }

    @PutMapping("/{eventId}")
    public EventDetailedDto editEvent(@PathVariable Long eventId,
                                      @RequestBody EventPostDto dto) {
        log.info("edit event id:{}, {}", eventId, dto);
        return eventService.editEvent(eventId, dto);
    }

    @PatchMapping("/{eventId}")
    public EventDetailedDto publishEvent(@PathVariable Long eventId,
                                         @RequestBody EventPostDto stateAction) {
        log.info("Публикация события: {} со статусом {}", eventId, stateAction.getStateAction());
        return eventService.publishEvent(eventId, stateAction);


//        public EventDetailedDto publishEvent(@PathVariable Long eventId,
//                                         @RequestBody String stateAction) {
//        log.info("Публикация события: {} со статусом {}", eventId, stateAction);
//        return eventService.publishEvent(eventId, stateAction);
    }
}