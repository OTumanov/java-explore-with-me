package ru.practicum.mnsvc.service;


import ru.practicum.mnsvc.dto.participation.ParticipationDto;

import java.util.List;

public interface ParticipationService {
    List<ParticipationDto> getInfoAboutAllParticipation(Long userId);

    ParticipationDto addParticipationQuery(Long userId, Long eventId);

    ParticipationDto cancelParticipation(Long userId, Long requestId);
}