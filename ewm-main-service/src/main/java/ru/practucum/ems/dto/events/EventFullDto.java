package ru.practucum.ems.dto.events;

import lombok.*;
import ru.practucum.ems.dto.category.CategoryDto;
import ru.practucum.ems.dto.users.UserShortDto;
import ru.practucum.ems.model.Location;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private Long id;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private String state;
    private String title;
    private Long views;
}