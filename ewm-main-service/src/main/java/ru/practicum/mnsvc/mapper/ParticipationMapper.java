package ru.practicum.mnsvc.mapper;

import ru.practicum.mnsvc.dto.participation.ParticipationDto;
import ru.practicum.mnsvc.exceptions.EnumParseException;
import ru.practicum.mnsvc.exceptions.NotFoundException;
import ru.practicum.mnsvc.model.Event;
import ru.practicum.mnsvc.model.Participation;
import ru.practicum.mnsvc.model.ParticipationState;
import ru.practicum.mnsvc.model.User;
import ru.practicum.mnsvc.repository.EventRepository;
import ru.practicum.mnsvc.repository.UserRepository;
import ru.practicum.mnsvc.utils.Util;

public class ParticipationMapper {

    private ParticipationMapper() {
    }

    public static Participation toModel(ParticipationDto dto, EventRepository eventRepo, UserRepository userRepo) {
        return Participation.builder()
                .created(DateTimeMapper.toDateTime(dto.getCreated()))
                .event(getEventById(dto.getEvent(), eventRepo))
                .id(dto.getId())
                .requester(getUserById(dto.getRequester(), userRepo))
                .state(parseApplicationState(dto.getState()))
                .build();
    }

    public static ParticipationDto toDto(Participation model) {
        return ParticipationDto.builder()
                .created(DateTimeMapper.toString(model.getCreated()))
                .event(model.getEvent().getId())
                .id(model.getId())
                .requester(model.getRequester().getId())
                .state(model.getState().toString())
                .build();
    }

    private static ParticipationState parseApplicationState(String state) {
        ParticipationState enumState;
        try {
            enumState = ParticipationState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EnumParseException("Недопустимое значение статуса заявки" + state);
        }
        return enumState;
    }

    private static Event getEventById(Long eventId, EventRepository repo) {
        Event event = repo.findById(eventId).orElse(null);
        if (event == null) {
            throw new NotFoundException(Util.getEventNotFoundMessage(eventId));
        }
        return event;
    }

    private static User getUserById(Long userId, UserRepository repo) {
        User user = repo.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException(Util.getUserNotFoundMessage(userId));
        }
        return user;
    }
}