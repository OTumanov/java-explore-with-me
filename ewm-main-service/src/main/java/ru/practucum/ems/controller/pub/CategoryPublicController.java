package ru.practucum.ems.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practucum.ems.dto.category.CategoryDto;
import ru.practucum.ems.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> findAll(@RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение всех категорий");
        return categoryService.findAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto findById(@PathVariable Long catId) {
        log.info("Получение информации о категории по ее идентификатору {}", catId);
        return categoryService.findById(catId);
    }
}