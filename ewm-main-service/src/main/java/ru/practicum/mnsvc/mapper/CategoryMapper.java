package ru.practicum.mnsvc.mapper;

import ru.practicum.mnsvc.dto.category.CategoryDto;
import ru.practicum.mnsvc.dto.category.NewCategoryDto;
import ru.practicum.mnsvc.model.Category;

public class CategoryMapper {

    public static Category toModel(NewCategoryDto dto) {
        return Category.builder()
                .id(null)
                .name(dto.getName())
                .build();
    }

    public static Category toModel(CategoryDto dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}