package ru.practicum.mnsvc.mapper;

import ru.practicum.mnsvc.dto.compile.CompilationPostDto;
import ru.practicum.mnsvc.dto.compile.CompilationResponseDto;
import ru.practicum.mnsvc.dto.events.EventShortDto;
import ru.practicum.mnsvc.exceptions.NotFoundException;
import ru.practicum.mnsvc.model.Compilation;
import ru.practicum.mnsvc.model.Event;
import ru.practicum.mnsvc.repository.EventRepository;
import ru.practicum.mnsvc.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {

    private CompilationMapper() {}

    public static Compilation toModel(CompilationPostDto dto, EventRepository repo) {
        return Compilation.builder()
                .events(replaceIdWithEvents(dto.getEvents(), repo))
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    public static CompilationResponseDto toResponseDto(Compilation compilation) {
        return CompilationResponseDto.builder()
                .events(replaceEventsWithEventShortDto(compilation.getEvents()))
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    private static List<Event> replaceIdWithEvents(List<Long> ids, EventRepository repo) {
        List<Event> events = new ArrayList<>();
        for (long eventId : ids) {
            Event event = repo.findById(eventId).orElse(null);
            if (event == null) {
                throw new NotFoundException(Util.getEventNotFoundMessage(eventId));
            }
            events.add(event);
        }
        return events;
    }

    private static List<EventShortDto> replaceEventsWithEventShortDto(List<Event> events) {
        List<EventShortDto> dtos = new ArrayList<>();
        for (Event ev : events) {
            dtos.add(EventMapper.toEventShortDto(ev));
        }
        return dtos;
    }
}