package ru.practicum.mnsvc.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.mnsvc.dto.category.CategoryDto;
import ru.practicum.mnsvc.dto.users.UserShortDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String eventDate;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}