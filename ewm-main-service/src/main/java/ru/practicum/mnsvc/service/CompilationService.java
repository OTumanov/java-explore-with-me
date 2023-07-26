package ru.practicum.mnsvc.service;

import ru.practicum.mnsvc.dto.compile.*;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size);

    CompilationDto findById(Long compId);

    CompilationDto addNewCompilation(NewCompilationDto dto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto);

//    void deleteEventFromCompilation(Long compId, Long eventId);
//
//    void addEventToCompilation(Long compId, Long eventId);
//
//    void unpinCompilation(Long compId);
//
//    void pinCompilation(Long compId);

}