package ru.practicum.mnsvc.dto.compile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.mnsvc.dto.events.EventShortDto;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CompilationResponseDto {
    private List<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    private String title;
}