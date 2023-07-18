package ru.practicum.mnsvc.dto.participation;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class ParticipationDto {
    private String created;
    private Long event;
    private Long id;
    private Long requester;
    private String state;
}