package ru.practicum.mnsvc.service;

import ru.practicum.mnsvc.dto.compile.CompilationPostDto;
import ru.practicum.mnsvc.dto.compile.CompilationResponseDto;

import java.util.List;

public interface CompilationService {
    List<CompilationResponseDto> findAll(Boolean pinned, Integer from, Integer size);

    CompilationResponseDto findById(Long compId);

    CompilationResponseDto addNewCompilation(CompilationPostDto dto);

    void deleteCompilation(Long compId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void addEventToCompilation(Long compId, Long eventId);

    void unpinCompilation(Long compId);

    void pinCompilation(Long compId);

}