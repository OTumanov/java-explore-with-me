package ru.practucum.ems.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practucum.ems.dto.events.EventFullDto;
import ru.practucum.ems.dto.events.EventShortDto;
import ru.practucum.ems.dto.events.NewEventDto;
import ru.practucum.ems.dto.events.UpdateEventUserRequest;
import ru.practucum.ems.dto.participation.EventRequestStatusUpdateRequest;
import ru.practucum.ems.dto.participation.EventRequestStatusUpdate;
import ru.practucum.ems.dto.participation.ParticipationRequestDto;
import ru.practucum.ems.service.EventService;

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
        log.info("Получение всех событий пользователя #{}", userId);
        return eventService.findEventsByInitiatorId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventsFullInfoByInitiatorId(@Positive @PathVariable Long userId,
                                                        @Positive @PathVariable Long eventId) {
        log.info("Получение информации о событии #{}, созданном пользователем #{}", eventId, userId);
        return eventService.findEventByIdAndOwnerId(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getInfoAboutEventParticipation(@Positive @PathVariable Long userId,
                                                                        @Positive @PathVariable Long eventId) {
        log.info("Получение запросов на участие в событии #{} от пользователя #{}", eventId, userId);
        return eventService.getInfoAboutEventParticipation(userId, eventId);
    }


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(@Positive @PathVariable Long userId,
                                  @Validated @RequestBody NewEventDto dto) {
        log.info("Добавление нового события пользователем #{} - \"{}\"", userId, dto.getTitle());
        return eventService.postEvent(userId, dto);
    }


    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent(@Positive @PathVariable Long userId,
                                   @Positive @PathVariable Long eventId,
                                   @Validated @RequestBody UpdateEventUserRequest dto) {
        log.info("Изменение события #{}, от пользователя #{} - \"{}\"", eventId, userId, dto.getTitle());
        return eventService.patchEvent(userId, eventId, dto);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdate confirmParticipation(@Positive @PathVariable Long userId,
                                                         @Positive @PathVariable Long eventId,
                                                         @RequestBody EventRequestStatusUpdateRequest dto) {
        log.info("Изменение статуса на \"{}\" заявки или заявок ##{} на участие в событии #{} от пользователя #{}", dto.getStatus(), dto.getRequestIds(), eventId, userId);
        return eventService.confirmParticipation(userId, eventId, dto);
    }
}