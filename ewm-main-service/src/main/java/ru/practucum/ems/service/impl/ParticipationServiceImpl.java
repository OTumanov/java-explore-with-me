package ru.practucum.ems.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practucum.ems.dto.participation.ParticipationRequestDto;
import ru.practucum.ems.exceptions.NotFoundException;
import ru.practucum.ems.mapper.ParticipationMapper;
import ru.practucum.ems.model.*;
import ru.practucum.ems.repository.EventRepository;
import ru.practucum.ems.repository.ParticipationRepository;
import ru.practucum.ems.repository.UserRepository;
import ru.practucum.ems.service.ParticipationService;

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
    public List<ParticipationRequestDto> getInfoAboutAllParticipation(Long userId) {
        checkUser(userId);
        List<Participation> allByRequesterId = participationRepository.findAllByRequesterId(userId);
        return allByRequesterId.stream().map(ParticipationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addParticipationQuery(Long userId, Long eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("id не заполнен!");
        }
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        Participation participation = participationRepository.findByEventIdAndRequesterId(eventId, userId).orElse(null);
        if (participation != null) {
            throw new DataIntegrityViolationException("Пользователь уже принял участие в событии");
        }
        if (!event.getState().equals(PublicationState.PUBLISHED)) {
            throw new DataIntegrityViolationException("Нельзя принять участие в неопубликованном событии");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityViolationException("Этот пользователь не может участвовать в событии");
        }
        int confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
        if (event.getParticipantLimit() != 0 && confirmedRequests >= event.getParticipantLimit()) {
            throw new DataIntegrityViolationException("Нельзя принять участие в событии - достигнут лимит участников");
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
    public ParticipationRequestDto cancelParticipation(Long requesterId, Long requestId) {
        Participation participation = checkEvent(requesterId, requestId);
        participation.setState(ParticipationState.CANCELED);

        return ParticipationMapper.toDto(participation);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Нет такого пользователя"));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Нет такого события"));
    }

    private Participation checkEvent(Long requesterId, Long requestId) {
        return participationRepository.findByRequesterIdAndId(requesterId, requestId)
                .orElseThrow(() -> new NotFoundException("Не найден запрос на участие от пользователя в этом событии"));
    }
}