package ru.practicum.mnsvc.controller.publicController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.compile.CompilationResponseDto;
import ru.practicum.mnsvc.service.CompilationService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationResponseDto> findAll(@NotNull @RequestParam Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("find all compilations from:{}, size:{}", from, size);
        return compilationService.findAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationResponseDto findById(@Positive @PathVariable Long compId) {
        log.info("find compilation by id:{}", compId);
        return compilationService.findById(compId);
    }
}