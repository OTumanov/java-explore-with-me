package ru.practucum.ems.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.sd.dto.UtilDto;
import ru.practicum.sd.dto.ViewStatsDto;
import ru.practicum.ssc.StatsClient;
import ru.practucum.ems.dto.events.*;
import ru.practucum.ems.dto.participation.EventRequestStatusUpdate;
import ru.practucum.ems.dto.participation.EventRequestStatusUpdateRequest;
import ru.practucum.ems.dto.participation.ParticipationRequestDto;
import ru.practucum.ems.exceptions.ForbiddenException;
import ru.practucum.ems.exceptions.NotFoundException;
import ru.practucum.ems.mapper.DateTimeMapper;
import ru.practucum.ems.mapper.EventMapper;
import ru.practucum.ems.mapper.ParticipationMapper;
import ru.practucum.ems.model.*;
import ru.practucum.ems.repository.*;
import ru.practucum.ems.service.EventService;
import ru.practucum.ems.utils.Constants;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practucum.ems.utils.Util.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final ParticipationRepository participationRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final StatsClient statsClient;

    private final String APP_NAME = "EWM-MAIN-SERVICE";

    @Override
    @Transactional
    public EventFullDto findEventById(Long id, String clientIp, String endpoint) {
        Event event = checkEvent(id);
        statsClient.hitRequest(EventMapper.endpointHitDto(APP_NAME, clientIp, endpoint));
        setViewsToOneEvent(event);
        if (!event.getState().equals(PublicationState.PUBLISHED)) {
            throw new NotFoundException("Событие должно быть опубликовано!");
        }

        return EventMapper.toEventFullDto(
                event,
                participationRepository.getConfirmedRequests(
                        event.getId(),
                        ParticipationState.CONFIRMED),
                event.getViews()
        );
    }

    @Override
    public List<EventShortDto> findEventsByInitiatorId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);

        return prepareDataAndGetEventShortDtoList(events);
    }

    @Override
    @Transactional
    public List<EventShortDto> findEvents(EventSearchParams params, String clientIp, String endpoint) {
        if (params.getRangeStart() != null && params.getRangeEnd() != null) {
            if (params.getRangeEnd().isBefore(params.getRangeStart())) {
                throw new IllegalArgumentException("Время окончания события не может быть до его начала!");
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
        Specification<Event> specification = getSpecification(params);
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize(), sort);
        List<Event> events = eventRepository.findAll(specification, pageable).toList();
        statsClient.hitRequest(EventMapper.endpointHitDto(APP_NAME, clientIp, endpoint));
        setViewsToEventList(events);

        return prepareDataAndGetEventShortDtoList(events);
    }

    @Override
    public List<EventFullDto> findEventsByConditions(EventSearchParams params) {
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Specification<Event> specification = getSpecification(params);
        List<Event> events = eventRepository.findAll(specification, pageable).toList();
        List<Long> eventIds = getEventIdsList(events);
        List<UtilDto> confirmedReqEventIdRelations = participationRepository.countParticipationByEventIds(eventIds, ParticipationState.CONFIRMED);
        List<UtilDto> viewsEventIdRelations = new ArrayList<>();
        for (Event event : events) {
            UtilDto eventDto = UtilDto.builder()
                    .count(event.getViews())
                    .entityId(event.getId())
                    .build();
            viewsEventIdRelations.add(eventDto);
        }

        return events.stream()
                .map((Event event) -> EventMapper.toEventFullDto(
                        event,
                        matchIntValueByEventId(confirmedReqEventIdRelations, event.getId()),
                        matchLongValueByEventId(viewsEventIdRelations, event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto findEventByIdAndOwnerId(Long userId, Long eventId) {
        Event event = checkEvent(eventId, userId);
        Integer confirmedRequests =
                participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
        Long views = event.getViews();

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    public List<ParticipationRequestDto> getInfoAboutEventParticipation(Long userId, Long eventId) {
        checkEvent(eventId, userId);
        List<Participation> participations = participationRepository.findAllByEventId(eventId);

        return participations.stream().map(ParticipationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto postEvent(Long userId, NewEventDto dto) {
        if (!checkDate(dto.getEventDate())) {
            throw new IllegalArgumentException("Событие не может наступить ранее двух часов от текущего времени!");
        }
        User initiator = checkUser(userId);
        Category category = checkCategory(Long.valueOf(dto.getCategory()));
        Event event = EventMapper.toModel(dto, initiator, category);
        Location location = locationRepository.save(event.getLocation());
        event.setLocation(location);
        event = eventRepository.save(event);
        Integer confirmedRequests =
                participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
        Long views = event.getViews();

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    @Transactional
    public EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = checkEvent(eventId, userId);

        if (dto.getEventDate() != null
                && (!dto.getEventDate().isEmpty())
                && DateTimeMapper.toDateTime(dto.getEventDate()).isBefore(LocalDateTime.now())) {
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
                participationRepository.getConfirmedRequests(
                        event.getId(),
                        ParticipationState.CONFIRMED),
                event.getViews());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdate confirmParticipation(Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {
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
            Participation participation = checkParticipation(Long.valueOf(pId));
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
            Long views = event.getViews();

            return EventMapper.toEventFullDto(event, confirmedRequests, views);
        } else if (dto.getStateAction() == StateAction.REJECT_EVENT) {
            Event event = checkEvent(eventId);
            if (event.getState().equals(PublicationState.PUBLISHED)) {
                throw new DataIntegrityViolationException("Нельзя отклонить опубликованное событие");
            }
            event.setState(PublicationState.CANCELED);
            event = eventRepository.save(event);
            Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
            Long views = event.getViews();

            return EventMapper.toEventFullDto(event, confirmedRequests, views);
        } else if (DateTimeMapper.toDateTime(dto.getEventDate()).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Нельзя указать время и дату из прошлого");
        } else if (dto.getStateAction() == null) {
            Event event = checkEvent(eventId);
            updateEvent(event, dto);
            event = eventRepository.save(event);
            Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
            Long views = event.getViews();

            return EventMapper.toEventFullDto(event, confirmedRequests, views);
        } else {
            throw new ForbiddenException("Недопустимые входные данные");
        }
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
            if (!checkDate(update.getEventDate())) {
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
            if (!checkDate(update.getEventDate())) {
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

    private void checkParticipationLimit(Event event, ParticipationRepository participationRepository) {
        if (event.getParticipantLimit().equals(participationRepository
                .getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED))) {
            List<Participation> allByEventIdAndState = participationRepository.findAllByEventIdAndState(event.getId(), ParticipationState.PENDING);
            for (Participation par : allByEventIdAndState) {
                par.setState(ParticipationState.REJECTED);
                participationRepository.save(par);
            }
        }
    }

    private List<EventShortDto> prepareDataAndGetEventShortDtoList(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<UtilDto> confirmedReqEventIdRelations = participationRepository.countParticipationByEventIds(eventIds, ParticipationState.CONFIRMED);
        List<UtilDto> viewsEventIdRelations = new ArrayList<>();
        for (Event event : events) {
            UtilDto eventDto = UtilDto.builder()
                    .count(event.getViews())
                    .entityId(event.getId())
                    .build();
            viewsEventIdRelations.add(eventDto);
        }

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

    private Participation checkParticipation(Long id) {
        return participationRepository.findById(id).orElseThrow(() -> new NotFoundException("Нет такого!"));
    }

    private boolean checkDate(String eventDate) {
        LocalDateTime date = DateTimeMapper.toDateTime(eventDate);
        return date.isAfter(LocalDateTime.now().plusHours(2));
    }

    private Specification<Event> getSpecification(EventSearchParams params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (params.getUserIds() != null) {
                for (Long userId : params.getUserIds()) {
                    predicates.add(criteriaBuilder.in(root.get("initiator").get("id")).value(userId));
                }
            }
            if (null != params.getText()) {
                criteriaBuilder.or(
                        criteriaBuilder.like(root.get("annotation"), "%" + params.getText() + "%"),
                        criteriaBuilder.like(root.get("description"), "%" + params.getText() + "%")
                );
            }
            if (null != params.getCategories() && !params.getCategories().isEmpty()) {
                for (Long catId : params.getCategories()) {
                    predicates.add(criteriaBuilder.in(root.get("category").get("id")).value(catId));
                }
            }
            if (null != params.getPaid()) {
                predicates.add(criteriaBuilder.equal(root.get("paid"), params.getPaid()));
            }
            if (null != params.getRangeStart()) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("publishedOn"), params.getRangeStart()));
            }
            if (null != params.getRangeEnd()) {
                predicates.add(criteriaBuilder.lessThan(root.get("publishedOn"), params.getRangeEnd()));
            }
            if (null != params.getOnlyAvailable() && params.getOnlyAvailable()) {
                predicates.add(criteriaBuilder.lessThan(root.get("participantLimit"), root.get("confirmedRequests")));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private void setViewsToOneEvent(Event event) {
        setViewsToEventList(List.of(event));
    }

    public void setViewsToEventList(List<Event> events) {
        List<Long> idEvents = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        String start = LocalDateTime.now().minusYears(100).format(Constants.formatter);
        String end = LocalDateTime.now().format(Constants.formatter);
        String eventsUri = "/events/";
        List<String> uris = idEvents.stream().map(id -> eventsUri + id).collect(Collectors.toList());
        List<ViewStatsDto> viewStatDtos = statsClient.getStats(start, end, uris, true);
        Map<Long, Long> hits = viewStatDtos.stream()
                .collect(Collectors.toMap(v -> Long.parseLong(v.getUri().substring(eventsUri.length())), ViewStatsDto::getHits));
        events.forEach(e -> e.setViews(hits.get(e.getId())));
    }
}