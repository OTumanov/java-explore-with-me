package ru.practicum.mnsvc.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.events.EventFullDto;
import ru.practicum.mnsvc.dto.events.UpdateEventAdminRequest;
import ru.practicum.mnsvc.model.EventSearchParams;
import ru.practicum.mnsvc.service.EventService;

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
        EventSearchParams searchParams = new EventSearchParams(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size
        );
        log.info("Поиск событий - {}", searchParams);
        return eventService.findEventsByConditions(searchParams);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto publishEvent(@Positive @PathVariable Long eventId,
                                     @Validated @RequestBody UpdateEventAdminRequest dto,
                                     HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        log.info("Подключение с ip-адреса: {}", clientIp);
        log.info("Подключение к эндпоинту: http://localhost:8080{}", endpoint);
        log.info("Редактирование данных события {} и его статуса (отклонение/публикация) - {}", eventId, dto);
        return eventService.publishEvent(eventId, dto, clientIp, endpoint);
    }
}