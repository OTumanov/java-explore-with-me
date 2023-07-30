package ru.practucum.ems.dto.events;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practucum.ems.model.Location;
import ru.practucum.ems.model.StateAction;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {

    @Length(min = 20, max = 2000, message = "От 20 до 2000 символов")
    private String annotation;
    private Long category;

    @Length(min = 20, max = 7000, message = "От 20 до 7000 символов")
    private String description;

    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;

    @Length(min = 3, max = 120, message = "От 3 до 120 символов")
    private String title;
}