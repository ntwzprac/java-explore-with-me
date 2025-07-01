package ru.practicum.mainservice.service;

import ru.practicum.mainservice.dto.request.NewCategoryDto;
import ru.practicum.mainservice.dto.response.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto dto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto dto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(Long catId);
} 