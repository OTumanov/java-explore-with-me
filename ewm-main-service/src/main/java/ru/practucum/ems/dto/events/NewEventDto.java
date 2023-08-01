package ru.practucum.ems.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practucum.ems.model.Location;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotEmpty
    @Length(min = 20, max = 2000, message = "Аннотация может содержать от 20 до 2000 символов")
    private String annotation;

    @NotNull
    private Integer category;

    @NotEmpty
    @Length(min = 20, max = 7000, message = "Описание может содержать от 20 до 7000 символов")
    private String description;

    @NotEmpty
    private String eventDate;

    @NotNull
    private Location location;

    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;

    @NotEmpty
    @Length(min = 3, max = 120, message = "Заголовок может содержать от 3 до 120 символов")
    private String title;
}