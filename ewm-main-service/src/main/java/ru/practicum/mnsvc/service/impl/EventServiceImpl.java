package ru.practicum.mnsvc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.client.dto.UtilDto;
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
import ru.practicum.mnsvc.repository.*;
import ru.practicum.mnsvc.service.EventService;
import ru.practicum.mnsvc.utils.Util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mnsvc.utils.EventServiceUtil.*;
import static ru.practicum.mnsvc.utils.Util.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final ParticipationRepository participationRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventClient client = new EventClient("http://localhost/8080", new RestTemplateBuilder());

    @Override
    @Transactional
    public List<EventShortDto> getEvents(EventSearchParams params, String clientIp, String endpoint) {
        Sort sort = Sort.unsorted();

        if (params.getSort() != null) {
            if (params.getSort().equals(EventSort.EVENT_DATE)) {
                sort = Sort.by(params.getSort().getValue());
            }
            if (params.getSort().equals(EventSort.VIEWS)) {
                sort = Sort.by(params.getSort().getValue());
            }
        }
        Specification<Event> specification = getSpecification(params, false);
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize(), sort);
        List<Event> events = eventRepository.findAll(specification, pageable).toList();

//        for(Event event : events) {
//            addHit(endpoint, clientIp, event.getId(), client);
//        }

        return prepareDataAndGetEventShortDtoList(events);
    }

    @Override
    @Transactional
    public EventDetailedDto findEventById(Long id, String clientIp, String endpoint) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(id)));

        addHit(endpoint, clientIp, id, client);

        return EventMapper.toEventDetailedDto(event,
                participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED),
                client.getViewsByEventId(event.getId()).getBody());
    }

    @Override
    public List<EventShortDto> findEventsByInitiatorId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        return prepareDataAndGetEventShortDtoList(events);
    }

    @Override
    @Transactional
    public EventDetailedDto patchEvent(Long userId, EventPatchDto dto) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(Util.getUserNotFoundMessage(userId)));
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(dto.getEventId())));

        if (event.getState().equals(PublicationState.PUBLISHED)) {
            throw new ForbiddenException("Only pending or canceled events can be changed");
        }

        updateEvent(event, dto, categoryRepository);
        if (event.getState().equals(PublicationState.CANCELED)) {
            event.setState(PublicationState.PENDING);
        }
        event = eventRepository.save(event);
        return EventMapper.toEventDetailedDto(event,
                participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED),
                client.getViewsByEventId(event.getId()).getBody());
    }

    @Override
    @Transactional
    public EventDetailedDto postEvent(Long userId, EventPostDto dto) {
        if (!isEventDateOk(dto.getEventDate())) {
            throw new ForbiddenException("Событие не может наступить ранее двух часов от текущего времени!");
        }
        User initiator = userRepository
                .findById(userId).orElseThrow(() -> new NotFoundException(Util.getUserNotFoundMessage(userId)));
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException(Util.getCategoryNotFoundMessage(dto.getCategory())));

        Event event = EventMapper.toModel(dto, initiator, category);
        Location location = locationRepository.save(event.getLocation());
        event.setLocation(location);
        event = eventRepository.save(event);
        Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
        Long views = client.getViewsByEventId(event.getId()).getBody();
        return EventMapper.toEventDetailedDto(event, confirmedRequests, views);
    }

    @Override
    public EventDetailedDto findEventByIdAndOwnerId(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId,
                userId).orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));
        Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
        Long views = client.getViewsByEventId(event.getId()).getBody();
        return EventMapper.toEventDetailedDto(event, confirmedRequests, views);
    }

    @Override
    @Transactional
    public EventDetailedDto cancelEventByIdAndOwnerId(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));

        if (!event.getState().equals(PublicationState.PENDING)) {
            throw new ForbiddenException("the event can only be canceled in the waiting state, current state: "
                    + event.getState());
        }
        event.setState(PublicationState.CANCELED);
        event = eventRepository.save(event);
        Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
        Long views = client.getViewsByEventId(event.getId()).getBody();
        return EventMapper.toEventDetailedDto(event, confirmedRequests, views);
    }

    @Override
    public List<ParticipationDto> getInfoAboutEventParticipation(Long userId, Long eventId) {
        eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));
        List<Participation> participations = participationRepository.findAllByEventId(eventId);
        return participations.stream().map(ParticipationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationDto confirmParticipation(Long userId, Long eventId, Long reqId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ForbiddenException("Confirmation of the participation is not required");
        }
        if (event.getParticipantLimit().equals(participationRepository
                .getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED))) {
            throw new ForbiddenException("the limit of participants in the event has been reached");
        }

        Participation participation = participationRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException(Util.getParticipationNotFoundMessage(reqId)));

        if (participation.getState().equals(ParticipationState.CONFIRMED)) {
            throw new ForbiddenException("the request for participation has already been confirmed");
        }

        participation.setState(ParticipationState.CONFIRMED);
        checkParticipationLimit(event, participationRepository);
        participation = participationRepository.save(participation);
        return ParticipationMapper.toDto(participation);
    }

    @Override
    @Transactional
    public ParticipationDto rejectParticipation(Long userId, Long eventId, Long reqId) {
        eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));

        Participation participation = participationRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException(Util.getParticipationNotFoundMessage(reqId)));

        if (participation.getState().equals(ParticipationState.REJECTED)) {
            throw new ForbiddenException("request for participation has already been rejected");
        }
        participation.setState(ParticipationState.REJECTED);
        participation = participationRepository.save(participation);
        return ParticipationMapper.toDto(participation);
    }

    @Override
    public List<EventDetailedDto> findEventsByConditions(EventSearchParams params) {
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Specification<Event> specification = getSpecification(params, false);
        List<Event> events = eventRepository.findAll(specification, pageable).toList();
        List<Long> eventIds = getEventIdsList(events);
        List<UtilDto> confirmedReqEventIdRelations = participationRepository
                .countParticipationByEventIds(eventIds, ParticipationState.CONFIRMED);
        List<UtilDto> viewsEventIdRelations = client.getViewsByEventIds(eventIds);

        return events.stream()
                .map((Event event) -> EventMapper.toEventDetailedDto(
                        event,
                        matchIntValueByEventId(confirmedReqEventIdRelations, event.getId()),
                        matchLongValueByEventId(viewsEventIdRelations, event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDetailedDto editEvent(Long eventId, EventPostDto dto) {
        Event editable = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));

        if (dto.getAnnotation() != null) {
            editable.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException(Util.getCategoryNotFoundMessage(dto.getCategory())));
            editable.setCategory(category);
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
        editable = eventRepository.save(editable);
        Integer confirmedRequests = participationRepository
                .getConfirmedRequests(editable.getId(), ParticipationState.CONFIRMED);
        Long views = client.getViewsByEventId(editable.getId()).getBody();
        return EventMapper.toEventDetailedDto(editable, confirmedRequests, views);
    }

    @Override
    @Transactional
    public EventDetailedDto publishEvent(Long eventId, EventPostDto dto) {

        if (dto.getStateAction() == StateAction.PUBLISH_EVENT) {

            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));

            if (!event.getState().equals(PublicationState.PENDING)) {
                throw new ForbiddenException("event must be in the publication waiting state");
            }
            if (!isMayPublish(event)) {
                throw new ForbiddenException("event date must be no earlier than an hour from the date of publication");
            }
            event.setPublishedOn(LocalDateTime.now());
            event.setState(PublicationState.PUBLISHED);
            event = eventRepository.save(event);
            Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
            Long views = client.getViewsByEventId(event.getId()).getBody();
            return EventMapper.toEventDetailedDto(event, confirmedRequests, views);
        } else if (dto.getStateAction() == StateAction.REJECT_EVENT) {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ForbiddenException(Util.getEventNotFoundMessage(eventId)));
            if (event.getState().equals(PublicationState.PUBLISHED)) {
                throw new ForbiddenException("not possible to reject a published event");
            }
            event.setState(PublicationState.CANCELED);
            event = eventRepository.save(event);
            Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
            Long views = client.getViewsByEventId(event.getId()).getBody();
            return EventMapper.toEventDetailedDto(event, confirmedRequests, views);
        } else {
            throw new ForbiddenException("invalid state action");
        }
    }


    private void addHit(String endpoint, String clientIp, Long eventId, EventClient client) {
        client.postHit(endpoint, clientIp, eventId);
    }


    private void updateEvent(Event event, EventPatchDto update, CategoryRepository categoryRepo) {
        if (update.getAnnotation() != null) {
            event.setAnnotation(update.getAnnotation());
        }
        if (update.getCategory() != null) {
            Category category = categoryRepo.findById((update.getCategory()))
                    .orElseThrow(() -> new NotFoundException(Util.getCategoryNotFoundMessage(update.getCategory())));
            event.setCategory(category);
        }
        if (update.getDescription() != null) {
            event.setDescription(update.getDescription());
        }
        if (update.getEventDate() != null) {
            if (!isEventDateOk(update.getEventDate())) {
                throw new ForbiddenException("the event can be changed no later than 2 hours before the start");
            }
            event.setEventDate(DateTimeMapper.toDateTime(update.getEventDate()));
        }
        if (update.getPaid() != null) {
            event.setPaid(update.getPaid());
        }
        if (update.getParticipantLimit() != null) {
            event.setParticipantLimit(update.getParticipantLimit());
        }
        if (update.getTitle() != null) {
            event.setTitle(update.getTitle());
        }
    }

    private void checkParticipationLimit(Event event, ParticipationRepository participationRepo) {
        if (event.getParticipantLimit().equals(participationRepo
                .getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED))) {
            List<Participation> participations = participationRepo
                    .findAllByEventIdAndState(event.getId(), ParticipationState.PENDING);

            for (Participation par : participations) {
                par.setState(ParticipationState.REJECTED);
                participationRepo.save(par);
            }
        }
    }

    private List<EventShortDto> prepareDataAndGetEventShortDtoList(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<UtilDto> confirmedReqEventIdRelations = participationRepository.countParticipationByEventIds(eventIds, ParticipationState.CONFIRMED);
        List<UtilDto> viewsEventIdRelations = client.getViewsByEventIds(eventIds);

        return events.stream()
                .map((Event event) -> EventMapper.toEventShortDto(
                        event,
                        matchIntValueByEventId(confirmedReqEventIdRelations, event.getId()),
                        matchLongValueByEventId(viewsEventIdRelations, event.getId())))
                .collect(Collectors.toList());
    }
}