package ru.practicum.mnsvc.service;

import ru.practicum.mnsvc.dto.events.EventDetailedDto;
import ru.practicum.mnsvc.dto.events.EventPatchDto;
import ru.practicum.mnsvc.dto.events.EventPostDto;
import ru.practicum.mnsvc.dto.events.EventShortDto;
import ru.practicum.mnsvc.dto.participation.EventRequestStatusUpdateDto;
import ru.practicum.mnsvc.dto.participation.ParticipationDto;
import ru.practicum.mnsvc.model.EventSearchParams;

import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(EventSearchParams criteria, String clientIp, String endpoint);

    EventDetailedDto findEventById(Long id, String clientIp, String endpoint);

    List<EventShortDto> findEventsByInitiatorId(Long userId, Integer from, Integer size);

    EventDetailedDto patchEvent(Long userId, Long eventId, EventPatchDto dto);

    EventDetailedDto postEvent(Long userId, EventPostDto dto);

    EventDetailedDto findEventByIdAndOwnerId(Long userId, Long eventId);

    EventDetailedDto patchEventByIdAndOwnerId(Long userId, Long eventId, EventPatchDto dto);

    List<ParticipationDto> getInfoAboutEventParticipation(Long userId, Long eventId);

    ParticipationDto confirmParticipation(Long userId, Long eventId, EventRequestStatusUpdateDto dto);

    ParticipationDto rejectParticipation(Long userId, Long eventId, Long reqId);

    List<EventDetailedDto> findEventsByConditions(EventSearchParams params);

    EventDetailedDto editEvent(Long eventId, EventPostDto dto);

    EventDetailedDto publishEvent(Long eventId, EventPostDto dto, String clientIp, String endpoint);

//    EventDetailedDto postRequest(Long userId, Long eventId);

//    EventDetailedDto rejectEvent(Long eventId);
}