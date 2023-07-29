package ru.practucum.ems.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practucum.ems.model.UpdateEventUserState;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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