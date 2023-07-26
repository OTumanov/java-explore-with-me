package ru.practicum.mnsvc.dto.events;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.mnsvc.model.Location;
import ru.practicum.mnsvc.model.StateAction;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class EventPostDto {
    @NotEmpty
    @Length(min = 20, max = 2000, message = "От 20 до 2000 символов")
    private String annotation;
    private Long category;

    @NotEmpty
    @Length(min = 20, max = 7000, message = "От 20 до 7000 символов")
    private String description;

    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;

    @NotEmpty
    @Length(min = 3, max = 120, message = "От 3 до 120 символов")
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