package ru.practucum.ems.mapper;

import ru.practucum.ems.dto.category.CategoryDto;
import ru.practucum.ems.dto.category.NewCategoryDto;
import ru.practucum.ems.model.Category;

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