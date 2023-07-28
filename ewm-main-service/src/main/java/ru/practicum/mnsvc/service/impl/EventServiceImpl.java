package ru.practicum.mnsvc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.client.dto.UtilDto;
import ru.practicum.mnsvc.dto.events.*;
import ru.practicum.mnsvc.dto.participation.EventRequestStatusUpdateResult;
import ru.practicum.mnsvc.dto.participation.EventRequestStatusUpdateRequest;
import ru.practicum.mnsvc.dto.participation.ParticipationRequestDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mnsvc.utils.EventServiceUtil.getSpecification;
import static ru.practicum.mnsvc.utils.EventServiceUtil.isEventDateOk;
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

        if (params.getRangeStart() != null && params.getRangeEnd() != null) {
            if (params.getRangeEnd().isBefore(params.getRangeStart())) {
                throw new IllegalArgumentException("Время окончания события не может быть перед стартовым!");
            }
        }

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

        for (Event event : events) {
            addHit(endpoint, clientIp, event.getId(), client);
        }

        return prepareDataAndGetEventShortDtoList(events);
    }

    @Override
    @Transactional
    public EventFullDto findEventById(Long id, String clientIp, String endpoint) {
        Event event = checkEvent(id);
        addHit(endpoint, clientIp, id, client);

        if (!event.getState().equals(PublicationState.PUBLISHED)) {
            throw new NotFoundException("Событие должно быть опубликовано!");
        }

        return EventMapper.toEventFullDto(event,
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
    public EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = checkEvent(eventId, userId);

        if (dto.getEventDate() != null && (!dto.getEventDate().isEmpty()) && DateTimeMapper.toDateTime(dto.getEventDate()).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Невозможно изменить событие находящееся в прошлом!");
        }

        if (event.getState().equals(PublicationState.PUBLISHED)) {
            throw new DataIntegrityViolationException("Отменить возможно только события в ожидании или отменённые");
        }

        if (event.getState().equals(PublicationState.CANCELED)) {
            event.setState(PublicationState.PENDING);
        }

        if (dto.getStateAction() != null && dto.getStateAction().equals(UpdateEventUserState.CANCEL_REVIEW)) {
            event.setState(PublicationState.CANCELED);
        }
        updateEvent(event, dto);
        event = eventRepository.save(event);
        return EventMapper.toEventFullDto(event,
                participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED),
                client.getViewsByEventId(event.getId()).getBody());
    }

    @Override
    @Transactional
    public EventFullDto postEvent(Long userId, NewEventDto dto) {
        if (!isEventDateOk(dto.getEventDate())) {
            throw new IllegalArgumentException("Событие не может наступить ранее двух часов от текущего времени!");
        }

        User initiator = checkUser(userId);
        Category category = checkCategory(Long.valueOf(dto.getCategory()));

        Event event = EventMapper.toModel(dto, initiator, category);
        Location location = locationRepository.save(event.getLocation());
        event.setLocation(location);
        event = eventRepository.save(event);
        Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
        Long views = client.getViewsByEventId(event.getId()).getBody();
        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    public EventFullDto findEventByIdAndOwnerId(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId,
                userId).orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));
        Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
        Long views = client.getViewsByEventId(event.getId()).getBody();
        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    @Transactional
    public EventFullDto patchEventByIdAndOwnerId(Long userId, Long eventId, EventPatchDto dto) {

        Event event = checkEvent(eventId, userId);

        if (!event.getState().equals(PublicationState.CANCELED) || !event.getRequestModeration()) {
            throw new IllegalArgumentException("Событие не может быть изменено! " +
                    "Изменить можно только отмененное событие или находящееся на модерации. Сейчас же статус у этого события:" + event.getState());
        }

        if (dto.getStateAction().equals(UpdateEventUserState.SEND_TO_REVIEW)) {
            event.setState(PublicationState.PENDING);
        }

        event = eventRepository.save(event);
        Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
        Long views = client.getViewsByEventId(event.getId()).getBody();
        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    public List<ParticipationRequestDto> getInfoAboutEventParticipation(Long userId, Long eventId) {
        eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));
        List<Participation> participations = participationRepository.findAllByEventId(eventId);
        return participations.stream().map(ParticipationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult confirmParticipation(Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {
        Event event = checkEvent(eventId, userId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Это событие не принадлежит данному пользователю!");
        }

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ForbiddenException("Подтверждение участия не требуется");
        }

        if (event.getParticipantLimit().equals(participationRepository
                .getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED))) {
            throw new DataIntegrityViolationException("Достигнут лимит участников в данном событии");
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (Integer pId : dto.getRequestIds()) {

            Participation participation = participationRepository.findById(Long.valueOf(pId)).orElseThrow(() -> new NotFoundException("Нет такого!"));

            if (participation.getState().equals(ParticipationState.CONFIRMED)) {
                throw new ForbiddenException("Заявка на участие уже утверждена");
            }

            if (dto.getStatus().equals(ParticEventStatus.REJECTED)) {
                participation.setState(ParticipationState.REJECTED);
            } else {
                participation.setState(ParticipationState.CONFIRMED);
            }


            checkParticipationLimit(event, participationRepository);
            participation = participationRepository.save(participation);

            if (participation.getState().equals(ParticipationState.CONFIRMED)) {
                confirmedRequests.add(ParticipationMapper.participationRequestDto(participation));
            } else {
                rejectedRequests.add(ParticipationMapper.participationRequestDto(participation));
            }
        }
        return ParticipationMapper.toEventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }


//    @Override
//    @Transactional
//        public ParticipationDto confirmParticipation(Long userId, Long eventId, EventRequestStatusUpdateDto dto) {
//
//        Event event = checkEvent(eventId, userId);
////
////        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
////            throw new ForbiddenException("Confirmation of the participation is not required");
////        }
////        if (event.getParticipantLimit().equals(participationRepository
////                .getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED))) {
////            throw new ForbiddenException("the limit of participants in the event has been reached");
////        }
////
//        Participation participation = participationRepository.findById(dto.getRequestIds().get(0))
//                .orElseThrow(() -> new NotFoundException(Util.getParticipationNotFoundMessage(dto.getRequestIds().get(0))));
//
////        if (participation.getState().equals(ParticipationState.CONFIRMED)) {
////            throw new ForbiddenException("the request for participation has already been confirmed");
////        }
//
//        participation.setState(ParticipationState.CONFIRMED);
//        checkParticipationLimit(event, participationRepository);
//        participation = participationRepository.save(participation);
//        return ParticipationMapper.toDto(participation);
//    }


    @Override
    @Transactional
    public ParticipationRequestDto rejectParticipation(Long userId, Long eventId, Long reqId) {
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
    public List<EventFullDto> findEventsByConditions(EventSearchParams params) {
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Specification<Event> specification = getSpecification(params, false);
        List<Event> events = eventRepository.findAll(specification, pageable).toList();
        List<Long> eventIds = getEventIdsList(events);
        List<UtilDto> confirmedReqEventIdRelations = participationRepository
                .countParticipationByEventIds(eventIds, ParticipationState.CONFIRMED);
        List<UtilDto> viewsEventIdRelations = client.getViewsByEventIds(eventIds);

        return events.stream()
                .map((Event event) -> EventMapper.toEventFullDto(
                        event,
                        matchIntValueByEventId(confirmedReqEventIdRelations, event.getId()),
                        matchLongValueByEventId(viewsEventIdRelations, event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto editEvent(Long eventId, UpdateEventAdminRequest dto) {
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
        return EventMapper.toEventFullDto(editable, confirmedRequests, views);
    }

    @Override
    @Transactional
    public EventFullDto publishEvent(Long eventId, UpdateEventAdminRequest dto, String clientIp, String endpoint) {
        if (dto.getStateAction() == StateAction.PUBLISH_EVENT) {
            Event event = checkEvent(eventId);

            if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
                throw new ForbiddenException("Событие не может быть отредактировано менее, чем за час до события");
            }
            if (!event.getState().equals(PublicationState.PENDING)) {
                throw new DataIntegrityViolationException("Событие должно быть в ожидании публикации");
            }

            event.setPublishedOn(LocalDateTime.now());
            updateEvent(event, dto);
            event.setState(PublicationState.PUBLISHED);
            event = eventRepository.save(event);
            Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
            Long views = client.getViewsByEventId(event.getId()).getBody();
            return EventMapper.toEventFullDto(event, confirmedRequests, views);
        } else if (dto.getStateAction() == StateAction.REJECT_EVENT) {
            Event event = checkEvent(eventId);
            if (event.getState().equals(PublicationState.PUBLISHED)) {
                throw new DataIntegrityViolationException("Нельзя отклонить опубликованное событие");
            }
            event.setState(PublicationState.CANCELED);
            event = eventRepository.save(event);
            Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
            Long views = client.getViewsByEventId(event.getId()).getBody();
            return EventMapper.toEventFullDto(event, confirmedRequests, views);
        } else if (DateTimeMapper.toDateTime(dto.getEventDate()).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Нельзя указать время и дату из прошлого");
        } else if (dto.getStateAction() == null) {
            Event event = checkEvent(eventId);
            updateEvent(event, dto);
            event = eventRepository.save(event);
            Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
            Long views = client.getViewsByEventId(event.getId()).getBody();
            return EventMapper.toEventFullDto(event, confirmedRequests, views);
        } else {
            throw new ForbiddenException("Недопустимые входные данные");
        }
    }


    private void addHit(String endpoint, String clientIp, Long eventId, EventClient client) {
        client.postHit(endpoint, clientIp, eventId);
    }


    private void updateEvent(Event event, UpdateEventAdminRequest update) {
        if (update.getAnnotation() != null) {
            event.setAnnotation(update.getAnnotation());
        }
        if (update.getCategory() != null) {
            Category category = categoryRepository.findById((update.getCategory()))
                    .orElseThrow(() -> new NotFoundException("Нет такой категории"));
            event.setCategory(category);
        }
        if (update.getDescription() != null) {
            event.setDescription(update.getDescription());
        }
        if (update.getEventDate() != null) {
            if (!isEventDateOk(update.getEventDate())) {
                throw new IllegalArgumentException("Событие можно изменить не менее, чем за 2 часа до события");
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

    private void updateEvent(Event event, UpdateEventUserRequest update) {
        if (update.getAnnotation() != null) {
            event.setAnnotation(update.getAnnotation());
        }
        if (update.getCategory() != null) {
            Category category = categoryRepository.findById((update.getCategory()))
                    .orElseThrow(() -> new NotFoundException("Нет такой категории"));
            event.setCategory(category);
        }
        if (update.getDescription() != null) {
            event.setDescription(update.getDescription());
        }
        if (update.getEventDate() != null) {
            if (!isEventDateOk(update.getEventDate())) {
                throw new IllegalArgumentException("Событие можно изменить не менее, чем за 2 часа до события");
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

    private Event checkEvent(long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Нет такого события!"));
    }

    private Event checkEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Нет такого события!"));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Нет такого пользователя!"));
    }

    private Category checkCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Нет такой категории"));
    }
}