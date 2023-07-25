package ru.practicum.mnsvc.dto.events;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.mnsvc.model.Location;
import ru.practicum.mnsvc.model.StateAction;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class EventPostDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;

    private StateAction stateAction;

    @Length(min = 3, max = 120)
    private String title;

//    @Override
//    public String toString() {
//        return "EventPostDto{" +
//                "annotation='" + annotation + '\'' +
//                ", category=" + category +
//                ", description='" + description + '\'' +
//                ", eventDate='" + eventDate + '\'' +
//                ", location=" + location +
//                ", paid=" + paid +
//                ", participantLimit=" + participantLimit +
//                ", requestModeration=" + requestModeration +
//                ", title='" + title + '\'' +
//                '}';
//    }
}