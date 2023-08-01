package ru.practucum.ems.service;


import ru.practucum.ems.dto.category.CategoryDto;
import ru.practucum.ems.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto findById(Long catId);

    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto addNewCategory(NewCategoryDto dto);

    CategoryDto patchCategory(CategoryDto dto, Long catId);

    void deleteCategory(Long catId);

}