package ru.practucum.ems.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practucum.ems.dto.category.CategoryDto;
import ru.practucum.ems.dto.users.UserShortDto;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    @NotEmpty
    private String annotation;

    @NotEmpty
    private CategoryDto category;
    private Integer confirmedRequests;

    @NotEmpty
    private String eventDate;
    private Long id;

    @NotEmpty
    private UserShortDto initiator;

    @NotEmpty
    private Boolean paid;

    @NotEmpty
    private String title;

    private Long views;
}