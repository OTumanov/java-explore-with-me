package ru.practucum.ems.mapper;

import ru.practucum.ems.dto.participation.EventRequestStatusUpdate;
import ru.practucum.ems.dto.participation.ParticipationRequestDto;
import ru.practucum.ems.model.Participation;

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

    public static EventRequestStatusUpdate toEventRequestStatusUpdateResult(
            List<ParticipationRequestDto> confirmedRequests, List<ParticipationRequestDto> rejectedRequests) {
        return EventRequestStatusUpdate.builder()
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