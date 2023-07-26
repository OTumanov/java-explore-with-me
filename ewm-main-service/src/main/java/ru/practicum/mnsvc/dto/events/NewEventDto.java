package ru.practicum.mnsvc.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.mnsvc.model.Location;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotEmpty
    @Length(min = 20, max = 200, message = "От 20 до 200 символов")
    private String annotation;

    @NotEmpty
    private Integer category;

    @NotEmpty
    @Length(min = 20, max = 7000, message = "От 20 до 7000 символов")
    private String description;

    @NotEmpty
    private String eventDate;

    @NotEmpty
    private Location location;

    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;

    @NotEmpty
    @Length(min = 3, max = 120, message = "От 3 до 120 символов")
    private String title;
}