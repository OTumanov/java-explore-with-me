package ru.practicum.mnsvc.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.events.EventDetailedDto;
import ru.practicum.mnsvc.dto.events.EventShortDto;
import ru.practicum.mnsvc.model.EventSearchParams;
import ru.practicum.mnsvc.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventPublicController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        log.info("Подключение с ip-адреса: {}", clientIp);
        log.info("Подключение к эндпоинту: http://localhost:8080{}", endpoint);

        EventSearchParams criteria = new EventSearchParams(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size
        );

        return eventService.getEvents(criteria, clientIp, endpoint);
    }

    @GetMapping("/{id}")
    public EventDetailedDto findEventById(@PathVariable Long id, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        log.info("client ip: {}", clientIp);
        log.info("endpoint path: {}", endpoint);
        return eventService.findEventById(id, clientIp, endpoint);
    }
}