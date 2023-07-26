package ru.practicum.mnsvc.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.events.EventDetailedDto;
import ru.practicum.mnsvc.dto.events.EventPatchDto;
import ru.practicum.mnsvc.dto.events.EventPostDto;
import ru.practicum.mnsvc.dto.events.EventShortDto;
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
@RequestMapping(path = "/users/{userId}")
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping("/events/{eventId}")
    public EventDetailedDto findEventByIdAndOwnerId(@Positive @PathVariable Long userId,
                                                    @Positive @PathVariable Long eventId) {
        log.info("Получить событие id:{} от пользователя id:{}", eventId, userId);
        return eventService.findEventByIdAndOwnerId(userId, eventId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationDto> getInfoAboutEventParticipation(@Positive @PathVariable Long userId,
                                                                 @Positive @PathVariable Long eventId) {
        log.info("Получить информацию о запросах на участие от пользователя id:{}, событие id:{}", eventId, userId);
        return eventService.getInfoAboutEventParticipation(userId, eventId);
    }

    @GetMapping("/events")
    public List<EventShortDto> findEventsByUserId(@Positive @PathVariable Long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Найти все события пользователя id:{} с {} размером :{}", userId, from, size);
        return eventService.findEventsByInitiatorId(userId, from, size);
    }

    @PostMapping("/events")
    public ResponseEntity<EventDetailedDto> postEvent(@Positive @PathVariable Long userId,
                                                      @Validated @RequestBody EventPostDto dto) {
        log.info("Добавить событие от пользователя id:{} с данными:{}", userId, dto);
        return new ResponseEntity<>(eventService.postEvent(userId, dto), HttpStatus.CREATED);
    }

//    @PatchMapping("/events")
//    public EventDetailedDto patchEvent(@Positive @PathVariable Long userId,
//                                       @Validated @RequestBody EventPatchDto dto) {
//        log.info("Изменить событие id:{} от пользователя id:{}", dto, userId);
//        return eventService.patchEvent(userId, dto);
//    }

    @PatchMapping("/events/{eventId}/requests")
    public ParticipationDto confirmParticipation(@Positive @PathVariable Long userId,
                                                 @Positive @PathVariable Long eventId,
                                                 @RequestBody EventRequestStatusUpdateDto dto) {
        log.info("Подтвердить запрос на участие от пользователя id:{}, событие id:{}, запрос id:{}", userId, eventId, dto);
        return eventService.confirmParticipation(userId, eventId, dto);
    }

//    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
//    public ParticipationDto confirmParticipation(@Positive @PathVariable Long userId,
//                                                 @Positive @PathVariable Long eventId,
//                                                 @Positive @PathVariable Long reqId) {
//        log.info("Подтвердить запрос на участие от пользователя id:{}, событие id:{}, запрос id:{}", userId, eventId, reqId);
//        return eventService.confirmParticipation(userId, eventId, reqId);
//    }

//    @PatchMapping("/{eventId}/requests/{reqId}/reject")
//    public ParticipationDto rejectParticipation(@Positive @PathVariable Long userId,
//                                                @Positive @PathVariable Long eventId,
//                                                @Positive @PathVariable Long reqId) {
//        log.info("Отклонить запрос на участие от пользователя id:{}, событие id:{}, запрос id:{}", userId, eventId, reqId);
//        return eventService.rejectParticipation(userId, eventId, reqId);
//    }

    @PatchMapping("/events/{eventId}")
    public EventDetailedDto patchEvent(@Positive @PathVariable Long userId,
                                       @Positive @PathVariable Long eventId,
                                       @Validated @RequestBody EventPatchDto dto) {
        log.info("Изменить событие id:{} от пользователя id:{}", eventId, userId);
        return eventService.patchEvent(userId, eventId, dto);
    }
}