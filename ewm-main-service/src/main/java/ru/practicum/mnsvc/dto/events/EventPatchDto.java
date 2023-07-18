package ru.practicum.mnsvc.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EventPatchDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Long id;
    private Boolean paid;
    private Integer participantLimit;
    private String title;

    @Override
    public String toString() {
        return "EventPatchDto{" +
                "annotation='" + annotation + '\'' +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", id=" + id +
                ", paid=" + paid +
                ", participantLimit=" + participantLimit +
                ", title='" + title + '\'' +
                '}';
    }
}