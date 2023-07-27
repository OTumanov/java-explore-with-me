package ru.practicum.mnsvc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mnsvc.dto.category.CategoryDto;
import ru.practicum.mnsvc.dto.category.NewCategoryDto;
import ru.practicum.mnsvc.exceptions.NotFoundException;
import ru.practicum.mnsvc.mapper.CategoryMapper;
import ru.practicum.mnsvc.model.Category;
import ru.practicum.mnsvc.repository.CategoryRepository;
import ru.practicum.mnsvc.service.CategoryService;
import ru.practicum.mnsvc.utils.Util;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(pageable).toList();
        return categories.stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long catId) {
        Category category = categoryRepository.findById(catId).orElse(null);
        if (category == null) {
            throw new NotFoundException(Util.getCategoryNotFoundMessage(catId));
        }
        return CategoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto patchCategory(CategoryDto dto, Long catId) {
        Category category = CategoryMapper.toModel(dto);
        category.setId(catId);
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto addNewCategory(NewCategoryDto dto) {
        Category newCat = CategoryMapper.toModel(dto);
        newCat = categoryRepository.save(newCat);
        return CategoryMapper.toDto(newCat);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        categoryRepository.deleteById(catId);
    }
}