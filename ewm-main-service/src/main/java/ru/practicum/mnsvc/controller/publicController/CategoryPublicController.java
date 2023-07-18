package ru.practicum.mnsvc.controller.publicController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.category.CategoryDto;
import ru.practicum.mnsvc.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryPublicController {

    public static final String DEFAULT_FROM = "0";
    public static final String DEFAULT_SIZE = "10";

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> findAll(@RequestParam(defaultValue = DEFAULT_FROM) Integer from,
                                     @RequestParam(defaultValue = DEFAULT_SIZE) Integer size) {
        log.info("find all categories from:{}, size:{}", from, size);
        return categoryService.findAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto findById(@PathVariable Long catId) {
        log.info("find category by id:{}", catId);
        return categoryService.findById(catId);
    }
}