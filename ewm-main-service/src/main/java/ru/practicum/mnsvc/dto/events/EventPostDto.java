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

    @Length(min = 20, max = 2000)
    private String annotation;
    private Long category;

    @Length(min = 20, max = 7000)
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