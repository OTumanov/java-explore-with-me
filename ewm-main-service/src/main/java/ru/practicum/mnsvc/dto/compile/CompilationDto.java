package ru.practicum.mnsvc.dto.compile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mnsvc.dto.events.EventShortDto;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private List<EventShortDto> events;

    @NotEmpty
    private Long id;

    @NotEmpty
    private Boolean pinned;

    @NotEmpty
    private String title;
}
