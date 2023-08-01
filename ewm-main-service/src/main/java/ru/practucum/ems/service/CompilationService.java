package ru.practucum.ems.service;

import ru.practucum.ems.dto.compile.CompilationDto;
import ru.practucum.ems.dto.compile.NewCompilationDto;
import ru.practucum.ems.dto.compile.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto findById(Long compId);

    List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size);

    CompilationDto addNewCompilation(NewCompilationDto dto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto);

    void deleteCompilation(Long compId);


}