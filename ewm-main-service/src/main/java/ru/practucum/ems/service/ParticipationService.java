package ru.practucum.ems.service;


import ru.practucum.ems.dto.participation.ParticipationRequestDto;

import java.util.List;

public interface ParticipationService {
    List<ParticipationRequestDto> getInfoAboutAllParticipation(Long userId);

    ParticipationRequestDto addParticipationQuery(Long userId, Long eventId);

    ParticipationRequestDto cancelParticipation(Long userId, Long requestId);
}