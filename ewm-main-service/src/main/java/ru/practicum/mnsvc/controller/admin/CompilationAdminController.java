package ru.practicum.mnsvc.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.compile.CompilationDto;
import ru.practicum.mnsvc.dto.compile.NewCompilationDto;
import ru.practicum.mnsvc.dto.compile.UpdateCompilationRequest;
import ru.practicum.mnsvc.service.CompilationService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addNewCompilation(@Validated @RequestBody NewCompilationDto dto) {
        log.info("Добавить новую подборку событий - {}", dto);
        return compilationService.addNewCompilation(dto);
    }


    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Validated @RequestBody UpdateCompilationRequest dto) {
        log.info("Обновить информация о подборке - {}", dto);
        return compilationService.updateCompilation(compId, dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("удаление подборки {}", compId);
        compilationService.deleteCompilation(compId);
    }

}