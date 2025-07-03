package ru.practicum.mainservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.NewCategoryDto;
import ru.practicum.mainservice.dto.response.CategoryDto;
import ru.practicum.mainservice.service.CategoryService;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto dto) {
        log.info("Adding new category: {}", dto.getName());
        CategoryDto result = categoryService.addCategory(dto);
        log.info("Category successfully added with id: {}", result.getId());
        return result;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Deleting category with id: {}", catId);
        categoryService.deleteCategory(catId);
        log.info("Category with id: {} successfully deleted", catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId, @RequestBody @Valid CategoryDto dto) {
        log.info("Updating category with id: {}, new name: {}", catId, dto.getName());
        CategoryDto result = categoryService.updateCategory(catId, dto);
        log.info("Category with id: {} successfully updated", catId);
        return result;
    }
}