package ru.practucum.ems.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practucum.ems.dto.compile.CompilationDto;
import ru.practucum.ems.service.CompilationService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> findAll(@RequestParam(required = false) Boolean pinned,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение всех подборок событий");
        return compilationService.findAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto findById(@Positive @PathVariable Long compId) {
        log.info("Получение подборки событий #{}", compId);
        return compilationService.findById(compId);
    }
}