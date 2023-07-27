package ru.practicum.mnsvc.dto.participation;

import lombok.Data;
import ru.practicum.mnsvc.model.ParticipationState;

import java.util.List;

@Data
public class EventRequestStatusUpdateDto {
    private List<Long> requestIds;
    private ParticipationState status;

}
