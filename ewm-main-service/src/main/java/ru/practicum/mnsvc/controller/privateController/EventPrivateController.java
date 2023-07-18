package ru.practicum.mnsvc.controller.privateController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.events.EventDetailedDto;
import ru.practicum.mnsvc.dto.events.EventPatchDto;
import ru.practicum.mnsvc.dto.events.EventPostDto;
import ru.practicum.mnsvc.dto.events.EventShortDto;
import ru.practicum.mnsvc.dto.participation.ParticipationDto;
import ru.practicum.mnsvc.service.EventService;
import ru.practicum.mnsvc.utils.PatchValidMarker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {

    public static final String DEFAULT_FROM = "0";
    public static final String DEFAULT_SIZE = "10";

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> findEventsByUserId(@Positive
                                                  @PathVariable Long userId,
                                                  @PositiveOrZero
                                                  @RequestParam(defaultValue = DEFAULT_FROM) Integer from,
                                                  @Positive
                                                  @RequestParam(defaultValue = DEFAULT_SIZE) Integer size) {
        log.info("find events by user id:{}, from:{}, size:{}", userId, from, size);
        return eventService.findEventsByInitiatorId(userId, from, size);
    }

    @PatchMapping
    public EventDetailedDto patchEvent(@Positive
                                       @PathVariable Long userId,
                                       @Validated({PatchValidMarker.class})
                                       @RequestBody EventPatchDto dto) {
        log.info("patch event {}, owner id:{}", dto, userId);
        return eventService.patchEvent(userId, dto);
    }

    @PostMapping
    public EventDetailedDto postEvent(@Positive
                                      @PathVariable Long userId,
                                      @Valid
                                      @RequestBody EventPostDto dto) {
        log.info("post event {}, owner id:{}", dto, userId);
        return eventService.postEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    public EventDetailedDto findEventByIdAndOwnerId(@Positive
                                                    @PathVariable Long userId,
                                                    @Positive
                                                    @PathVariable Long eventId) {
        log.info("find event by id:{} and owner id:{}", eventId, userId);
        return eventService.findEventByIdAndOwnerId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDetailedDto canselEventByIdAndOwnerId(@Positive
                                                      @PathVariable Long userId,
                                                      @Positive
                                                      @PathVariable Long eventId) {
        log.info("cansel event by id:{} and owner id:{}", eventId, userId);
        return eventService.cancelEventByIdAndOwnerId(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationDto> getInfoAboutEventParticipation(@Positive
                                                                 @PathVariable Long userId,
                                                                 @Positive
                                                                 @PathVariable Long eventId) {
        log.info("get info about event participation event id:{}, owner id:{}", eventId, userId);
        return eventService.getInfoAboutEventParticipation(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ParticipationDto confirmParticipation(@Positive
                                                 @PathVariable Long userId,
                                                 @Positive
                                                 @PathVariable Long eventId,
                                                 @Positive
                                                 @PathVariable Long reqId) {
        log.info("confirm participation user id:{}, event id:{}, request id:{}", userId, eventId, reqId);
        return eventService.confirmParticipation(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ParticipationDto rejectParticipation(@Positive
                                                @PathVariable Long userId,
                                                @Positive
                                                @PathVariable Long eventId,
                                                @Positive
                                                @PathVariable Long reqId) {
        log.info("reject participation user id:{}, event id:{}, request id:{}", userId, eventId, reqId);
        return eventService.rejectParticipation(userId, eventId, reqId);
    }
}