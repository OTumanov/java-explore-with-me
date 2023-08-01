package ru.practucum.ems.service;

import ru.practucum.ems.dto.events.*;
import ru.practucum.ems.dto.participation.EventRequestStatusUpdateRequest;
import ru.practucum.ems.dto.participation.EventRequestStatusUpdate;
import ru.practucum.ems.dto.participation.ParticipationRequestDto;
import ru.practucum.ems.model.EventSearchParams;

import java.util.List;

public interface EventService {
    List<EventShortDto> findEvents(EventSearchParams criteria, String clientIp, String endpoint);

    EventFullDto findEventById(Long id, String clientIp, String endpoint);

    EventFullDto findEventByIdAndOwnerId(Long userId, Long eventId);

    List<EventFullDto> findEventsByConditions(EventSearchParams params);

    List<EventShortDto> findEventsByInitiatorId(Long userId, Integer from, Integer size);

    List<ParticipationRequestDto> getInfoAboutEventParticipation(Long userId, Long eventId);

    EventRequestStatusUpdate confirmParticipation(Long userId, Long eventId, EventRequestStatusUpdateRequest dto);

    EventFullDto publishEvent(Long eventId, UpdateEventAdminRequest dto, String clientIp, String endpoint);

    EventFullDto postEvent(Long userId, NewEventDto dto);

    EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest dto);

}