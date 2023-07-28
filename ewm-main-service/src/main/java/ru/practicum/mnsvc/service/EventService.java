package ru.practicum.mnsvc.service;

import ru.practicum.mnsvc.dto.events.*;
import ru.practicum.mnsvc.dto.participation.EventRequestStatusUpdateRequest;
import ru.practicum.mnsvc.dto.participation.EventRequestStatusUpdateResult;
import ru.practicum.mnsvc.dto.participation.ParticipationRequestDto;
import ru.practicum.mnsvc.model.EventSearchParams;

import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(EventSearchParams criteria, String clientIp, String endpoint);

    EventFullDto findEventById(Long id, String clientIp, String endpoint);

    List<EventShortDto> findEventsByInitiatorId(Long userId, Integer from, Integer size);

    EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest dto);

    EventFullDto postEvent(Long userId, NewEventDto dto);

    EventFullDto findEventByIdAndOwnerId(Long userId, Long eventId);

    List<ParticipationRequestDto> getInfoAboutEventParticipation(Long userId, Long eventId);

    EventRequestStatusUpdateResult confirmParticipation(Long userId, Long eventId, EventRequestStatusUpdateRequest dto);

    List<EventFullDto> findEventsByConditions(EventSearchParams params);

    EventFullDto publishEvent(Long eventId, UpdateEventAdminRequest dto, String clientIp, String endpoint);
}