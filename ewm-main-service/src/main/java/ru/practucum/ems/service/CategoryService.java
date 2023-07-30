package ru.practucum.ems.service;



import ru.practucum.ems.dto.category.CategoryDto;
import ru.practucum.ems.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto findById(Long catId);

    CategoryDto patchCategory(CategoryDto dto, Long catId);

    CategoryDto addNewCategory(NewCategoryDto dto);

    void deleteCategory(Long catId);

}