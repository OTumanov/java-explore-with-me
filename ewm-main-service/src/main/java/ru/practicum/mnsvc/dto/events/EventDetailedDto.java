package ru.practicum.mnsvc.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import ru.practicum.ewm_ms.model.Location;
import ru.practicum.mnsvc.dto.category.CategoryDto;
import ru.practicum.mnsvc.dto.users.UserShortDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EventDetailedDto {
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