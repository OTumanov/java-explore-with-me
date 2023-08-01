package ru.practucum.ems.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practucum.ems.dto.events.EventFullDto;
import ru.practucum.ems.dto.events.EventShortDto;
import ru.practucum.ems.model.EventSearchParams;
import ru.practucum.ems.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventPublicController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> searchEvents(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(required = false) String sort,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        log.info("Получение событий с возможностью фильтрации");
        EventSearchParams criteria = new EventSearchParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        return eventService.findEvents(criteria, clientIp, endpoint);
    }

    @GetMapping("/{id}")
    public EventFullDto findEventById(@PathVariable Long id, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        log.info("Получение информации об опубликованном событии #{}", id);

        return eventService.findEventById(id, clientIp, endpoint);
    }
}