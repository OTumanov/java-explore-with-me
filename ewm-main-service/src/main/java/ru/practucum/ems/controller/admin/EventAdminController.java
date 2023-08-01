package ru.practucum.ems.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practucum.ems.dto.events.EventFullDto;
import ru.practucum.ems.dto.events.UpdateEventAdminRequest;
import ru.practucum.ems.model.EventSearchParams;
import ru.practucum.ems.service.EventService;

import javax.servlet.http.HttpServletRequest;
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
    public List<EventFullDto> findEventsByConditions(@RequestParam(required = false) List<Long> users,
                                                     @RequestParam(required = false) List<String> states,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(required = false) String rangeStart,
                                                     @RequestParam(required = false) String rangeEnd,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        EventSearchParams searchParams = new EventSearchParams(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("Поиск событий - \"{}\"", searchParams.toString().substring(0, 100) + "...");
        return eventService.findEventsByConditions(searchParams);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto publishEvent(@Positive @PathVariable Long eventId,
                                     @Validated @RequestBody UpdateEventAdminRequest dto,
                                     HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        log.info("Редактирование данных события #{} и его статуса (отклонение/публикация) - \"{}\"", eventId, dto.getStateAction());
        return eventService.publishEvent(eventId, dto, clientIp, endpoint);
    }
}