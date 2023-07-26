package ru.practicum.mnsvc.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.mnsvc.dto.compile.CompilationDto;
import ru.practicum.mnsvc.dto.compile.NewCompilationDto;
import ru.practicum.mnsvc.dto.events.EventShortDto;
import ru.practicum.mnsvc.model.Compilation;
import ru.practicum.mnsvc.model.Event;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationMapper {

    public static Compilation toModel(NewCompilationDto dto, List<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    public static CompilationDto toResponseDto(Compilation compilation, List<EventShortDto> eventsDto) {
        return CompilationDto.builder()
                .events(eventsDto)
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}