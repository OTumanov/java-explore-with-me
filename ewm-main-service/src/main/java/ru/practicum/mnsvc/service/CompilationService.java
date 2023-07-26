package ru.practicum.mnsvc.service;

import ru.practicum.mnsvc.dto.compile.CompilationDto;
import ru.practicum.mnsvc.dto.compile.CompilationPostDto;
import ru.practicum.mnsvc.dto.compile.CompilationResponseDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size);

    CompilationDto findById(Long compId);

    CompilationResponseDto addNewCompilation(CompilationPostDto dto);

    void deleteCompilation(Long compId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void addEventToCompilation(Long compId, Long eventId);

    void unpinCompilation(Long compId);

    void pinCompilation(Long compId);

}