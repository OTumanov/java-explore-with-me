package ru.practicum.mnsvc.dto.participation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ParticipationDto {
    private String created;
    private Long event;
    private Long id;
    private Long requester;
    private String state;
}