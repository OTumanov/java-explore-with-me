package ru.practucum.ems.service;

import ru.practucum.ems.dto.compile.CompilationDto;
import ru.practucum.ems.dto.compile.NewCompilationDto;
import ru.practucum.ems.dto.compile.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size);

    CompilationDto findById(Long compId);

    CompilationDto addNewCompilation(NewCompilationDto dto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto);

}