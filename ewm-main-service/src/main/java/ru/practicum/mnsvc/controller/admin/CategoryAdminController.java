package ru.practicum.mnsvc.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.category.CategoryDto;
import ru.practicum.mnsvc.dto.category.NewCategoryDto;
import ru.practicum.mnsvc.service.CategoryService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto addNewCategory(@RequestBody NewCategoryDto dto) {
        log.info("Добавление новой категории {}", dto);
        return categoryService.addNewCategory(dto);
    }

    @PatchMapping
    public CategoryDto patchCategory(@Validated @RequestBody CategoryDto dto) {
        log.info("Изменение категории {}", dto);
        return categoryService.patchCategory(dto);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Удаление категории {}", catId);
        categoryService.deleteCategory(catId);
    }
}