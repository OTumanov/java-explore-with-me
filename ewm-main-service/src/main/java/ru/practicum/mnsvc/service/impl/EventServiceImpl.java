package ru.practicum.mnsvc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mnsvc.dto.events.EventDetailedDto;
import ru.practicum.mnsvc.dto.events.EventPatchDto;
import ru.practicum.mnsvc.dto.events.EventPostDto;
import ru.practicum.mnsvc.dto.events.EventShortDto;
import ru.practicum.mnsvc.dto.participation.ParticipationDto;
import ru.practicum.mnsvc.exceptions.ForbiddenException;
import ru.practicum.mnsvc.exceptions.NotFoundException;
import ru.practicum.mnsvc.mapper.DateTimeMapper;
import ru.practicum.mnsvc.mapper.EventMapper;
import ru.practicum.mnsvc.mapper.ParticipationMapper;
import ru.practicum.mnsvc.model.*;
import ru.practicum.mnsvc.repository.CategoryRepository;
import ru.practicum.mnsvc.repository.EventRepository;
import ru.practicum.mnsvc.repository.ParticipationRepository;
import ru.practicum.mnsvc.repository.UserRepository;
import ru.practicum.mnsvc.service.EventService;
import ru.practicum.mnsvc.utils.Util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mnsvc.utils.EventServiceUtil.*;
import ru.practicum.ewm_ms.client.EventClient;



@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventClient client;
    private final ParticipationRepository participationRepo;
    private final CategoryRepository categoryRepo;
    private final EventRepository eventRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional
    public List<EventShortDto> getEvents(EventSearchParams params, String clientIp, String endpoint) {
        Sort sort = Sort.by(params.getSort().toString());
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize(), sort);
        Specification<Event> specification = getSpecification(params, true);
        List<Event> events = eventRepo.findAll(specification, pageable).toList();
        addViewForEach(events, eventRepo);
        client.postHit(endpoint, clientIp);
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDetailedDto findEventById(Long id, String clientIp, String endpoint) {
        Event event = eventRepo.findById(id).orElse(null);
        if (event == null) {
            throw new NotFoundException(Util.getEventNotFoundMessage(id));
        }
        event = addView(event, eventRepo);
        client.postHit(endpoint, clientIp);
        return EventMapper.toEventDetailedDto(event);
    }

    @Override
    public List<EventShortDto> findEventsByInitiatorId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepo.findAllByInitiatorId(userId, pageable);
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDetailedDto patchEvent(Long userId, EventPatchDto dto) {
        Util.checkIfUserExists(userId, userRepo);
        Event event = Util.checkIfEventExists(dto.getId(), eventRepo);

        if (event.getState().equals(PublicationState.PUBLISHED)) {
            throw new ForbiddenException("Only pending or canceled events can be changed");
        }

        updateEvent(event, dto, categoryRepo);
        if (event.getState().equals(PublicationState.CANCEL)) {
            event.setState(PublicationState.PENDING);
        }
        event = eventRepo.save(event);
        return EventMapper.toEventDetailedDto(event);
    }

    @Override
    @Transactional
    public EventDetailedDto postEvent(Long userId, EventPostDto dto) {
        if (isEventDateOk(dto.getEventDate())) {
            throw new ForbiddenException("the event cannot be earlier than 2 hours from the current time");
        }
        Event event = EventMapper.toModel(dto, userId, categoryRepo, userRepo);
        event = eventRepo.save(event);
        return EventMapper.toEventDetailedDto(event);
    }

    @Override
    public EventDetailedDto findEventByIdAndOwnerId(Long userId, Long eventId) {
        Event event = eventRepo.findByIdAndInitiatorId(eventId, userId).orElse(null);
        if (event == null) {
            throw new NotFoundException(Util.getEventNotFoundMessage(eventId));
        }
        return EventMapper.toEventDetailedDto(event);
    }

    @Override
    @Transactional
    public EventDetailedDto cancelEventByIdAndOwnerId(Long userId, Long eventId) {
        Event event = eventRepo.findByIdAndInitiatorId(eventId, userId).orElse(null);
        if (event == null) {
            throw new NotFoundException(Util.getEventNotFoundMessage(eventId));
        }
        if (!event.getState().equals(PublicationState.PENDING)) {
            throw new ForbiddenException("the event can only be canceled in the waiting state, current state: "
                    + event.getState());
        }
        event.setState(PublicationState.CANCEL);
        event = eventRepo.save(event);
        return EventMapper.toEventDetailedDto(event);
    }

    @Override
    public List<ParticipationDto> getInfoAboutEventParticipation(Long userId, Long eventId) {
        Event event = eventRepo.findByIdAndInitiatorId(eventId, userId).orElse(null);
        if (event == null) {
            throw new NotFoundException(Util.getEventNotFoundMessage(eventId));
        }
        List<Participation> participations = participationRepo.findAllByEventId(eventId);
        return participations.stream().map(ParticipationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationDto confirmParticipation(Long userId, Long eventId, Long reqId) {
        Event event = eventRepo.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ForbiddenException("Confirmation of the participation is not required");
        }
        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ForbiddenException("the limit of participants in the event has been reached");
        }

        Participation participation = participationRepo.findById(reqId)
                .orElseThrow(() -> new NotFoundException(Util.getParticipationNotFoundMessage(reqId)));

        if (participation.getState().equals(ParticipationState.CONFIRMED)) {
            throw new ForbiddenException("the request for participation has already been confirmed");
        }

        participation.setState(ParticipationState.CONFIRMED);
        increaseConfirmedRequest(event, eventRepo);
        checkParticipationLimit(event, participationRepo);
        participation = participationRepo.save(participation);
        return ParticipationMapper.toDto(participation);
    }

    @Override
    @Transactional
    public ParticipationDto rejectParticipation(Long userId, Long eventId, Long reqId) {
        eventRepo.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));

        Participation participation = participationRepo.findById(reqId)
                .orElseThrow(() -> new NotFoundException(Util.getParticipationNotFoundMessage(reqId)));

        if (participation.getState().equals(ParticipationState.REJECT)) {
            throw new ForbiddenException("request for participation has already been rejected");
        }
        participation.setState(ParticipationState.REJECT);
        participation = participationRepo.save(participation);
        return ParticipationMapper.toDto(participation);
    }

    @Override
    public List<EventDetailedDto> findEventsByConditions(EventSearchParams params) {
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Specification<Event> specification = getSpecification(params, false);

        List<Event> events = eventRepo.findAll(specification, pageable).toList();
        return events.stream().map(EventMapper::toEventDetailedDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDetailedDto editEvent(Long eventId, EventPostDto dto) {
        Event editable = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));

        if (dto.getAnnotation() != null) {
            editable.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            editable.setCategory(Util.mapIdToCategory(dto.getCategory(), categoryRepo));
        }
        if (dto.getDescription() != null) {
            editable.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null && isEventDateOk(dto.getEventDate())) {
            editable.setEventDate(DateTimeMapper.toDateTime(dto.getEventDate()));
        }
        if (dto.getLocation() != null) {
            editable.setLocation(dto.getLocation());
        }
        if (dto.getPaid() != null) {
            editable.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            editable.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            editable.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            editable.setTitle(dto.getTitle());
        }
        editable = eventRepo.save(editable);
        return EventMapper.toEventDetailedDto(editable);
    }

    @Override
    @Transactional
    public EventDetailedDto publishEvent(Long eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));

        if (!event.getState().equals(PublicationState.PENDING)) {
            throw new ForbiddenException("event must be in the publication waiting state");
        }
        if (!isMayPublish(event)) {
            throw new ForbiddenException("event date must be no earlier than an hour from the date of publication");
        }
        event.setPublishedOn(LocalDateTime.now());
        event.setState(PublicationState.PUBLISHED);
        event = eventRepo.save(event);
        return EventMapper.toEventDetailedDto(event);
    }

    @Override
    @Transactional
    public EventDetailedDto rejectEvent(Long eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new ForbiddenException(Util.getEventNotFoundMessage(eventId)));
        if (event.getState().equals(PublicationState.PUBLISHED)) {
            throw new ForbiddenException("not possible to reject a published event");
        }
        event.setState(PublicationState.REJECTED);
        event = eventRepo.save(event);
        return EventMapper.toEventDetailedDto(event);
    }
}