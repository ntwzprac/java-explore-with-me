package ru.practicum.mainservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.response.CategoryDto;
import ru.practicum.mainservice.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.info("Getting categories with parameters: from={}, size={}", from, size);
        List<CategoryDto> categories = categoryService.getCategories(from, size);
        log.info("Found {} categories", categories.size());
        return categories;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        log.info("Getting category with id: {}", catId);
        CategoryDto category = categoryService.getCategory(catId);
        log.info("Category found: {}", category.getName());
        return category;
    }
}