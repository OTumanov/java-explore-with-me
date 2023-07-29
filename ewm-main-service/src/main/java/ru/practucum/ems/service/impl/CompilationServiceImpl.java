package ru.practucum.ems.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.sd.dto.UtilDto;
import ru.practucum.ems.dto.compile.CompilationDto;
import ru.practucum.ems.dto.compile.NewCompilationDto;
import ru.practucum.ems.dto.compile.UpdateCompilationRequest;
import ru.practucum.ems.dto.events.EventShortDto;
import ru.practucum.ems.exceptions.NotFoundException;
import ru.practucum.ems.mapper.CompilationMapper;
import ru.practucum.ems.mapper.EventMapper;
import ru.practucum.ems.model.Compilation;
import ru.practucum.ems.model.Event;
import ru.practucum.ems.model.ParticipationState;
import ru.practucum.ems.repository.CompilationRepository;
import ru.practucum.ems.repository.EventRepository;
import ru.practucum.ems.repository.ParticipationRepository;
import ru.practucum.ems.service.CompilationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practucum.ems.utils.Util.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final ParticipationRepository participationRepository;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto findById(Long compId) {
        Compilation compilation = checkCompilation(compId);

        List<EventShortDto> eventShortDtoList = getEventShortDtos(compilation);
        return CompilationMapper.toResponseDto(compilation, eventShortDtoList);
    }

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
        List<EventShortDto> eventShortDtoList = getEventShortDtoList(events);

        return CompilationMapper.toResponseDto(compilation, eventShortDtoList);
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

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    private List<EventShortDto> getEventShortDtos(Compilation compilation) {
        return getEventShortDtoList(compilation.getEvents());
    }

    private List<EventShortDto> getEventShortDtoList(List<Event> events) {
        List<Long> eventIds = getEventIdsList(events);
        List<UtilDto> confirmedReqEventIdRelations = participationRepository
                .countParticipationByEventIds(eventIds, ParticipationState.CONFIRMED);

        List<UtilDto> viewsEventIdRelations = new ArrayList<>();

        for (Event event : events) {
            UtilDto eventDto = UtilDto.builder()
                    .count(event.getViews())
                    .entityId(event.getId())
                    .build();
            viewsEventIdRelations.add(eventDto);
        }

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
        return compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Такая подборка не найдена"));
    }
}