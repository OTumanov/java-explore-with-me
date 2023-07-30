package ru.practucum.ems.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practucum.ems.dto.category.CategoryDto;
import ru.practucum.ems.dto.category.NewCategoryDto;
import ru.practucum.ems.service.CategoryService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addNewCategory(@Validated @RequestBody NewCategoryDto dto) {
        log.info("Добавление новой категории {}", dto);
        return categoryService.addNewCategory(dto);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto patchCategory(@Validated @RequestBody CategoryDto dto,
                                     @PathVariable Long catId) {
        log.info("Изменение категории {} на {}", catId, dto);
        return categoryService.patchCategory(dto, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Удаление категории {}", catId);
        categoryService.deleteCategory(catId);
    }
}