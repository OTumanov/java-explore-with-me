package ru.practicum.mnsvc.mapper;

import ru.practicum.mnsvc.dto.events.EventDetailedDto;
import ru.practicum.mnsvc.dto.events.EventPostDto;
import ru.practicum.mnsvc.dto.events.EventShortDto;
import ru.practicum.mnsvc.exceptions.NotFoundException;
import ru.practicum.mnsvc.model.*;
import ru.practicum.mnsvc.repository.CategoryRepository;
import ru.practicum.mnsvc.repository.UserRepository;
import ru.practicum.mnsvc.utils.Util;

import java.time.LocalDateTime;

public class EventMapper {

    private EventMapper() {
    }

    public static Event toModel(EventPostDto dto, User initiator, Category category) {
        Event event = Event.builder()
                .annotation(dto.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(dto.getDescription())
                .eventDate(DateTimeMapper.toDateTime(dto.getEventDate()))
                .id(null)
                .initiator(initiator)
                .location(dto.getLocation())
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .publishedOn(null)
                .requestModeration(dto.getRequestModeration())
                .state(PublicationState.PENDING)
                .title(dto.getTitle())
                .build();
        event.setRequestModeration(event.getRequestModeration() == null || event.getRequestModeration());
        return event;
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
//                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(DateTimeMapper.toString(event.getEventDate()))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
//                .views(event.getViews())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event, Integer confirmedRequests, Long views) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(DateTimeMapper.toString(event.getEventDate()))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public static EventDetailedDto toEventDetailedDto(Event event) {
        EventState state = EventState.PENDING;
        if (event.getState() == PublicationState.PUBLISHED) {
            state = EventState.PUBLISHED;
        }
        if (event.getState() == PublicationState.CANCELED)
            state = EventState.CANCELED;

        return EventDetailedDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
//                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(DateTimeMapper.toString(event.getCreatedOn()))
                .description(event.getDescription())
                .eventDate(DateTimeMapper.toString(event.getEventDate()))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(DateTimeMapper.toString(event.getPublishedOn()))
                .requestModeration(event.getRequestModeration())
                .state(state)
                .title(event.getTitle())
//                .views(event.getViews())
                .build();
    }

    public static EventDetailedDto toEventDetailedDto(Event event, Integer confirmedRequests, Long views) {
        EventState state = EventState.PENDING;
        if (event.getState() == PublicationState.PUBLISHED) {
            state = EventState.PUBLISHED;
        }
        if (event.getState() == PublicationState.CANCELED)
            state = EventState.CANCELED;

        EventDetailedDto dto = EventDetailedDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(DateTimeMapper.toString(event.getCreatedOn()))
                .description(event.getDescription())
                .eventDate(DateTimeMapper.toString(event.getEventDate()))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(state)
                .title(event.getTitle())
                .views(views)
                .build();

        if (event.getPublishedOn() != null) {
            dto.setPublishedOn(DateTimeMapper.toString(event.getPublishedOn()));
        }
        return dto;
    }

    private static Category matchCategory(Long id, CategoryRepository repo) {
        Category category = repo.findById(id).orElse(null);
        if (category == null) {
            throw new NotFoundException(Util.getCategoryNotFoundMessage(id));
        }
        return category;
    }

    private static User matchUser(Long userId, UserRepository repo) {
        User user = repo.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException(Util.getUserNotFoundMessage(userId));
        }
        return user;
    }
}