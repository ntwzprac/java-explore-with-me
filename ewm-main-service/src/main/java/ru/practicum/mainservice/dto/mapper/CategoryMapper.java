package ru.practicum.mainservice.dto.mapper;

import ru.practicum.mainservice.dto.request.NewCategoryDto;
import ru.practicum.mainservice.dto.response.CategoryDto;
import ru.practicum.mainservice.model.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        if (category == null) return null;
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toEntity(NewCategoryDto dto) {
        if (dto == null) return null;
        return Category.builder()
                .name(dto.getName())
                .build();
    }
} 