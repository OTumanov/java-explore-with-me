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
import ru.practicum.mnsvc.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mnsvc.utils.Util.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final ParticipationRepository participationRepository;
    private final CompilationRepository compilationRepository;
    private final CompEventsRepository compEventsRepository;
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
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(Util.getCompilationNotFoundMessage(compId)));

        List<EventShortDto> eventDtos = getEventShortDtos(compilation);
        return CompilationMapper.toResponseDto(compilation, eventDtos);
    }

    @Override
    public CompilationDto addNewCompilation(NewCompilationDto dto) {

        if (dto.getEvents().isEmpty()) {
            return null;
        }
        List<Event> events = new ArrayList<>();

        for (Integer eventId : dto.getEvents()) {
            events.add(checkEvent(Long.valueOf(eventId)));
            Compilation compilation = CompilationMapper.toModel(dto, events);
            compilation = compilationRepository.save(compilation);
            List<EventShortDto> eventDtos = getEventShortDtoList(events);
            return CompilationMapper.toResponseDto(compilation, eventDtos);
        }

        return null;
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        return null;
    }

//    @Override
//    @Transactional
//    public void deleteEventFromCompilation(Long compId, Long eventId) {
//        compilationRepository
//                .findById(compId).orElseThrow(() -> new NotFoundException(Util.getCompilationNotFoundMessage(compId)));
//        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));
//        compEventsRepository.deleteByCompilationIdAndEventId(compId, eventId);
//    }

//    @Override
//    @Transactional
//    public void addEventToCompilation(Long compId, Long eventId) {
//        compilationRepository
//                .findById(compId).orElseThrow(() -> new NotFoundException(Util.getCompilationNotFoundMessage(compId)));
//        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(Util.getEventNotFoundMessage(eventId)));
//        CompEvent compEvent = new CompEvent(compId, eventId);
//        compEventsRepository.save(compEvent);
//    }
//
//    @Override
//    @Transactional
//    public void unpinCompilation(Long compId) {
//        Compilation compilation = compilationRepository
//                .findById(compId).orElseThrow(() -> new NotFoundException(Util.getCompilationNotFoundMessage(compId)));
//        compilation.setPinned(false);
//    }
//
//    @Override
//    @Transactional
//    public void pinCompilation(Long compId) {
//        Compilation compilation = compilationRepository
//                .findById(compId).orElseThrow(() -> new NotFoundException(Util.getCompilationNotFoundMessage(compId)));
//        compilation.setPinned(true);
//    }

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
}