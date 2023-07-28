package ru.practicum.mnsvc.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.events.EventFullDto;
import ru.practicum.mnsvc.dto.events.EventShortDto;
import ru.practicum.mnsvc.dto.events.NewEventDto;
import ru.practicum.mnsvc.dto.events.UpdateEventUserRequest;
import ru.practicum.mnsvc.dto.participation.EventRequestStatusUpdateDto;
import ru.practicum.mnsvc.dto.participation.ParticipationDto;
import ru.practicum.mnsvc.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping()
    public List<EventShortDto> findEventsShortInfoByInitiatorId(@Positive @PathVariable Long userId,
                                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение событий, добавленных текущим пользователем {}", userId);
        return eventService.findEventsByInitiatorId(userId, from, size);
    }


    @GetMapping("/{eventId}")
    public EventFullDto findEventsFullInfoByInitiatorId(@Positive @PathVariable Long userId,
                                                        @Positive @PathVariable Long eventId) {
        log.info("Получение полной информации о событии {}, добавленном текущим пользователем {}", eventId, userId);
        return eventService.findEventByIdAndOwnerId(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationDto> getInfoAboutEventParticipation(@Positive @PathVariable Long userId,
                                                                 @Positive @PathVariable Long eventId) {
        log.info("Получение информации о запросах на участие в событии {} текущего пользователя {}", eventId, userId);
        return eventService.getInfoAboutEventParticipation(userId, eventId);
    }


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(@Positive @PathVariable Long userId,
                                  @Validated @RequestBody NewEventDto dto) {
        log.info("Добавление нового события {} {}", userId, dto);
        return eventService.postEvent(userId, dto);
    }


    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent(@Positive @PathVariable Long userId,
                                   @Positive @PathVariable Long eventId,
                                   @Validated @RequestBody UpdateEventUserRequest dto) {
        log.info("Изменения события {}, добавленного текущим пользователем {} -- {}", eventId, userId, dto);
        return eventService.patchEvent(userId, eventId, dto);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationDto confirmParticipation(@Positive @PathVariable Long userId,
                                                 @Positive @PathVariable Long eventId,
                                                 @RequestBody EventRequestStatusUpdateDto dto) {
        log.info("Изменение статуса (подтверждение или отмена - {}) заявок на участие в событии {} от текущего пользователя {}", dto, eventId, userId);
        return eventService.confirmParticipation(userId, eventId, dto);
    }
}