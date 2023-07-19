package ru.practicum.mnsvc.dto.events;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.mnsvc.utils.CommonValidMarker;
import ru.practicum.mnsvc.utils.PatchValidMarker;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class EventPatchDto {

    @Length(min = 20, max = 2000, groups = CommonValidMarker.class)
    private String annotation;
    private Long category;

    @Length(min = 20, max = 7000, groups = CommonValidMarker.class)
    private String description;
    private String eventDate;

    @NotNull(groups = PatchValidMarker.class)
    private Long eventId;
    private Boolean paid;
    private Integer participantLimit;

    @Length(min = 3, max = 120, groups = CommonValidMarker.class)
    private String title;

//    @Override
//    public String toString() {
//        return "EventPatchDto{" +
//                "annotation='" + annotation + '\'' +
//                ", category=" + category +
//                ", description='" + description + '\'' +
//                ", eventDate='" + eventDate + '\'' +
//                ", id=" + eventId +
//                ", paid=" + paid +
//                ", participantLimit=" + participantLimit +
//                ", title='" + title + '\'' +
//                '}';
//    }
}