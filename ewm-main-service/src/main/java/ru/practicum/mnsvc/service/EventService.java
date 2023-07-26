package ru.practicum.mnsvc.service;

import ru.practicum.mnsvc.dto.events.*;
import ru.practicum.mnsvc.dto.participation.EventRequestStatusUpdateDto;
import ru.practicum.mnsvc.dto.participation.ParticipationDto;
import ru.practicum.mnsvc.model.EventSearchParams;

import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(EventSearchParams criteria, String clientIp, String endpoint);

    EventFullDto findEventById(Long id, String clientIp, String endpoint);

    List<EventShortDto> findEventsByInitiatorId(Long userId, Integer from, Integer size);

    EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest dto);

    EventFullDto postEvent(Long userId, NewEventDto dto);

    EventFullDto findEventByIdAndOwnerId(Long userId, Long eventId);

    EventFullDto patchEventByIdAndOwnerId(Long userId, Long eventId, EventPatchDto dto);

    List<ParticipationDto> getInfoAboutEventParticipation(Long userId, Long eventId);

    ParticipationDto confirmParticipation(Long userId, Long eventId, EventRequestStatusUpdateDto dto);

    ParticipationDto rejectParticipation(Long userId, Long eventId, Long reqId);

    List<EventFullDto> findEventsByConditions(EventSearchParams params);

    EventFullDto editEvent(Long eventId, UpdateEventAdminRequest dto);

    EventFullDto publishEvent(Long eventId, UpdateEventAdminRequest dto, String clientIp, String endpoint);

//    EventDetailedDto postRequest(Long userId, Long eventId);

//    EventDetailedDto rejectEvent(Long eventId);
}