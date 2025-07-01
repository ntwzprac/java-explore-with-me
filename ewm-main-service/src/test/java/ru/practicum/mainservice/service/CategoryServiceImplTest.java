package ru.practicum.mainservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.mainservice.dto.response.CategoryDto;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.repository.CategoryRepository;
import ru.practicum.mainservice.service.impl.CategoryServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("cat1");
    }

    @Test
    void getCategoryById_ShouldReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        CategoryDto result = categoryService.getCategory(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("cat1", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_NotFound_ShouldThrowException() {
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> categoryService.getCategory(2L));
        verify(categoryRepository, times(1)).findById(2L);
    }
} 