package ru.practicum.mnsvc.mapper;

import ru.practicum.mnsvc.dto.participation.EventRequestStatusUpdateResult;
import ru.practicum.mnsvc.dto.participation.ParticipationRequestDto;
import ru.practicum.mnsvc.model.Participation;

import java.util.List;

public class ParticipationMapper {

    public static ParticipationRequestDto toDto(Participation model) {
        return ParticipationRequestDto.builder()
                .created(DateTimeMapper.toString(model.getCreated()))
                .event(model.getEvent().getId())
                .id(model.getId())
                .requester(model.getRequester().getId())
                .status(model.getState().toString())
                .build();
    }

    public static EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(
            List<ParticipationRequestDto> confirmedRequests, List<ParticipationRequestDto> rejectedRequests) {
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    public static ParticipationRequestDto participationRequestDto(Participation participation) {
        return ParticipationRequestDto.builder()
                .id(participation.getId())
                .created(DateTimeMapper.toString(participation.getCreated()))
                .event(participation.getEvent().getId())
                .requester(participation.getRequester().getId())
                .status(participation.getState().toString())
                .build();
    }
}