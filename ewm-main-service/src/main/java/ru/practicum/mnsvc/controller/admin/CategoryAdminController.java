package ru.practicum.mnsvc.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CategoryDto> addNewCategory(@Validated @RequestBody NewCategoryDto dto) {
        log.info("Добавление новой категории {}", dto);
        return new ResponseEntity<>(categoryService.addNewCategory(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> patchCategory(@Validated @RequestBody CategoryDto dto,
                                                     @PathVariable Long catId) {
        log.info("Изменение категории {} на {}", catId, dto);
        return new ResponseEntity<>(categoryService.patchCategory(dto, catId), HttpStatus.OK);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Удаление категории {}", catId);
        categoryService.deleteCategory(catId);
    }
}