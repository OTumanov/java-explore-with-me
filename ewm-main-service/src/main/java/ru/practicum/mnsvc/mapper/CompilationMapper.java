package ru.practicum.mnsvc.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.mnsvc.dto.compile.CompilationDto;
import ru.practicum.mnsvc.dto.compile.NewCompilationDto;
import ru.practicum.mnsvc.dto.events.EventShortDto;
import ru.practicum.mnsvc.model.Compilation;
import ru.practicum.mnsvc.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationMapper {

    public static Compilation toModel(NewCompilationDto dto, List<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(Objects.requireNonNullElse(dto.getPinned(), false))
                .title(dto.getTitle())
                .build();
    }

    public static Compilation toModel(NewCompilationDto dto) {
        return Compilation.builder()
                .events(new ArrayList<>())
                .pinned(Objects.requireNonNullElse(dto.getPinned(), false))
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

    public static CompilationDto toResponseDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(new ArrayList<>())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}