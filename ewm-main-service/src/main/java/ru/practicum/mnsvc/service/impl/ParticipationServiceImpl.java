package ru.practicum.mnsvc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mnsvc.dto.participation.ParticipationDto;
import ru.practicum.mnsvc.exceptions.NotFoundException;
import ru.practicum.mnsvc.mapper.ParticipationMapper;
import ru.practicum.mnsvc.model.*;
import ru.practicum.mnsvc.repository.EventRepository;
import ru.practicum.mnsvc.repository.ParticipationRepository;
import ru.practicum.mnsvc.repository.UserRepository;
import ru.practicum.mnsvc.service.ParticipationService;
import ru.practicum.mnsvc.utils.Util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationDto> getInfoAboutAllParticipation(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(Util.getUserNotFoundMessage(userId)));
        List<Participation> participations = participationRepository.findAllByRequesterId(userId);
        return participations.stream().map(ParticipationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationDto addParticipationQuery(Long userId, Long eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("event id is null");
        }
        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        Participation participation =
                participationRepository.findByEventIdAndRequesterId(eventId, userId).orElse(null);

        if (participation != null) {
            throw new DataIntegrityViolationException("Пользователь уже принял участие в событии");
        }
        if (!event.getState().equals(PublicationState.PUBLISHED)) {
            throw new DataIntegrityViolationException("Нельзя принять участие в событии, которое еще не опубликовано");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityViolationException("Пользователь с этим id не может участвовать в событии");
        }

        int confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);

        if (event.getParticipantLimit() != 0 && confirmedRequests >= event.getParticipantLimit()) {
            throw new DataIntegrityViolationException("Нельзя принять участие в событии, которое превышает лимит участников");
        }

        Participation newParticipation = Participation.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .build();

        if (event.getRequestModeration().equals(false) || event.getParticipantLimit() == 0) {
            newParticipation.setState(ParticipationState.CONFIRMED);
        } else {
            newParticipation.setState(ParticipationState.PENDING);
        }

        newParticipation = participationRepository.save(newParticipation);
        return ParticipationMapper.toDto(newParticipation);
    }

    @Override
    @Transactional
    public ParticipationDto cancelParticipation(Long requesterId, Long requestId) {
        Participation participation = participationRepository.findByRequesterIdAndId(requesterId, requestId)
                .orElseThrow(() -> new NotFoundException("Participation not found requesterId: "
                        + requesterId + " requestId: " + requestId));
        participation.setState(ParticipationState.REJECTED);
        return ParticipationMapper.toDto(participation);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с id: " + userId));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Нет события с id: " + eventId));
    }
}