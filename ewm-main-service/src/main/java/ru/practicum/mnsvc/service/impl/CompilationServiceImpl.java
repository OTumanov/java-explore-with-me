package ru.practicum.mnsvc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mnsvc.dto.compile.CompilationPostDto;
import ru.practicum.mnsvc.dto.compile.CompilationResponseDto;
import ru.practicum.mnsvc.exceptions.NotFoundException;
import ru.practicum.mnsvc.mapper.CompilationMapper;
import ru.practicum.mnsvc.model.CompEvent;
import ru.practicum.mnsvc.model.Compilation;
import ru.practicum.mnsvc.repository.CompEventsRepository;
import ru.practicum.mnsvc.repository.CompilationRepository;
import ru.practicum.mnsvc.repository.EventRepository;
import ru.practicum.mnsvc.service.CompilationService;
import ru.practicum.mnsvc.utils.Util;


import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mnsvc.utils.Util.checkIfCompilationExists;
import static ru.practicum.mnsvc.utils.Util.checkIfEventExists;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepo;
    private final CompEventsRepository compEventsRepo;
    private final EventRepository eventRepo;

    @Override
    public List<CompilationResponseDto> findAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepo.findAllByPinned(pinned, pageable);
        return compilations
                .stream()
                .map(CompilationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationResponseDto findById(Long compId) {
        Compilation compilation = compilationRepo.findById(compId).orElse(null);
        if (compilation == null) {
            throw new NotFoundException(Util.getCompilationNotFoundMessage(compId));
        }
        return CompilationMapper.toResponseDto(compilation);
    }

    @Override
    @Transactional
    public CompilationResponseDto addNewCompilation(CompilationPostDto dto) {
        Compilation compilation = CompilationMapper.toModel(dto, eventRepo);
        compilation = compilationRepo.save(compilation);
        return CompilationMapper.toResponseDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepo.deleteById(compId);
    }

    @Override
    @Transactional
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        checkIfCompilationExists(compId, compilationRepo);
        checkIfEventExists(eventId, eventRepo);
        compEventsRepo.deleteByCompilationIdAndEventId(compId, eventId);
    }

    @Override
    @Transactional
    public void addEventToCompilation(Long compId, Long eventId) {
        checkIfCompilationExists(compId, compilationRepo);
        checkIfEventExists(eventId, eventRepo);
        CompEvent compEvent = new CompEvent(compId, eventId);
        compEventsRepo.save(compEvent);
    }

    @Override
    @Transactional
    public void unpinCompilation(Long compId) {
        Compilation compilation = checkIfCompilationExists(compId, compilationRepo);
        if (compilation.getPinned()) {
            compilation.setPinned(false);
            compilationRepo.save(compilation);
        }
    }

    @Override
    @Transactional
    public void pinCompilation(Long compId) {
        Compilation compilation = checkIfCompilationExists(compId, compilationRepo);
        if (!compilation.getPinned()) {
            compilation.setPinned(true);
            compilationRepo.save(compilation);
        }
    }
}