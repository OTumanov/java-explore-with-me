package ru.practicum.mnsvc.mapper;

import ru.practicum.mnsvc.dto.events.EventFullDto;
import ru.practicum.mnsvc.dto.events.EventShortDto;
import ru.practicum.mnsvc.dto.events.NewEventDto;
import ru.practicum.mnsvc.model.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class EventMapper {

    public static Event toModel(NewEventDto dto, User initiator, Category category) {
        Event event = Event.builder()
                .annotation(dto.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(dto.getDescription())
                .eventDate(DateTimeMapper.toDateTime(dto.getEventDate()))
                .id(null)
                .initiator(initiator)
                .location(dto.getLocation())
                .paid(Objects.requireNonNullElse(dto.getPaid(), false))
                .participantLimit(Objects.requireNonNullElse(dto.getParticipantLimit(), 0))
                .publishedOn(null)
                .requestModeration(Objects.requireNonNullElse(dto.getRequestModeration(), true))
                .state(PublicationState.PENDING)
                .title(dto.getTitle())
                .build();
        event.setRequestModeration(event.getRequestModeration() == null || event.getRequestModeration());
        return event;
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

    public static EventFullDto toEventFullDto(Event event, Integer confirmedRequests, Long views) {
        String state = String.valueOf(EventState.PENDING);
        if (event.getState() == PublicationState.PUBLISHED) {
            state = String.valueOf(EventState.PUBLISHED);
        }
        if (event.getState() == PublicationState.CANCELED)
            state = String.valueOf(EventState.CANCELED);

        EventFullDto dto = EventFullDto.builder()
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
}