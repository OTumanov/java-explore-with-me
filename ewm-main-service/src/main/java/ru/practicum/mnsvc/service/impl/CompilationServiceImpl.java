package ru.practicum.mnsvc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.client.dto.UtilDto;
import ru.practicum.mnsvc.dto.compile.CompilationDto;
import ru.practicum.mnsvc.dto.compile.NewCompilationDto;
import ru.practicum.mnsvc.dto.compile.UpdateCompilationRequest;
import ru.practicum.mnsvc.dto.events.EventShortDto;
import ru.practicum.mnsvc.exceptions.NotFoundException;
import ru.practicum.mnsvc.mapper.CompilationMapper;
import ru.practicum.mnsvc.mapper.EventMapper;
import ru.practicum.mnsvc.model.Compilation;
import ru.practicum.mnsvc.model.Event;
import ru.practicum.mnsvc.model.ParticipationState;
import ru.practicum.mnsvc.repository.CompEventsRepository;
import ru.practicum.mnsvc.repository.CompilationRepository;
import ru.practicum.mnsvc.repository.EventRepository;
import ru.practicum.mnsvc.repository.ParticipationRepository;
import ru.practicum.mnsvc.service.CompilationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.mnsvc.utils.Util.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final ParticipationRepository participationRepository;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventClient client = new EventClient("http://localhost/8080", new RestTemplateBuilder());

    @Override
    public List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).toList();
        }

        return compilations
                .stream()
                .map((Compilation compilation) -> CompilationMapper.toResponseDto(
                        compilation, getEventShortDtos(compilation)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto findById(Long compId) {
        Compilation compilation = checkCompilation(compId);

        List<EventShortDto> eventDtos = getEventShortDtos(compilation);
        return CompilationMapper.toResponseDto(compilation, eventDtos);
    }

    @Transactional
    @Override
    public CompilationDto addNewCompilation(NewCompilationDto dto) {

        if (dto.getEvents() == null || dto.getEvents().isEmpty()) {
            Compilation compilation = CompilationMapper.toModel(dto);
            compilation = compilationRepository.save(compilation);

            return CompilationMapper.toResponseDto(compilation);
        }

        List<Event> events = new ArrayList<>();

        for (Integer eventId : dto.getEvents()) {
            events.add(checkEvent(Long.valueOf(eventId)));
        }
        Compilation compilation = CompilationMapper.toModel(dto, events);
        compilation = compilationRepository.save(compilation);
        List<EventShortDto> eventDtos = getEventShortDtoList(events);

        return CompilationMapper.toResponseDto(compilation, eventDtos);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        Compilation compilation = checkCompilation(compId);

        List<Event> events = new ArrayList<>();

        if (dto.getEvents() != null) {
            for (Integer eventId : dto.getEvents()) {
                events.add(checkEvent(Long.valueOf(eventId)));
            }
        }

        compilation.setEvents(events);
        compilation.setPinned(Objects.requireNonNullElse(dto.isPinned(), compilation.getPinned()));
        compilation.setTitle(Objects.requireNonNullElse(dto.getTitle(), compilation.getTitle()));
        compilationRepository.save(compilation);

        return CompilationMapper.toResponseDto(compilation, getEventShortDtoList(events));
    }

    private List<EventShortDto> getEventShortDtos(Compilation compilation) {
        return getEventShortDtoList(compilation.getEvents());
    }

    private List<EventShortDto> getEventShortDtoList(List<Event> events) {
        List<Long> eventIds = getEventIdsList(events);
        List<UtilDto> confirmedReqEventIdRelations = participationRepository
                .countParticipationByEventIds(eventIds, ParticipationState.CONFIRMED);
        List<UtilDto> viewsEventIdRelations = client.getViewsByEventIds(eventIds);
        return events.stream()
                .map((Event event) -> EventMapper.toEventShortDto(
                        event,
                        matchIntValueByEventId(confirmedReqEventIdRelations, event.getId()),
                        matchLongValueByEventId(viewsEventIdRelations, event.getId())))
                .collect(Collectors.toList());
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Нет такого события"));
    }

    private Compilation checkCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Такая подборка не найдена"));
    }
}