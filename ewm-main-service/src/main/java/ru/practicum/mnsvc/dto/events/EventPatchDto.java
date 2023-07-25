package ru.practicum.mnsvc.dto.events;

import lombok.*;
import ru.practicum.mnsvc.model.UpdateEventUserState;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class EventPatchDto {

    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Long eventId;
    private Boolean paid;
    private Integer participantLimit;
    private String title;
    private Boolean requestModeration;
    private UpdateEventUserState stateAction;

}