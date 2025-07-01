package ru.practicum.mainservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.mainservice.dto.request.NewCategoryDto;
import ru.practicum.mainservice.dto.response.CategoryDto;
import ru.practicum.mainservice.service.CategoryService;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCategoryController.class)
class AdminCategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CategoryService categoryService;

    @Test
    void addCategory_success() throws Exception {
        NewCategoryDto request = new NewCategoryDto();
        request.setName("Test Category");
        CategoryDto response = new CategoryDto();
        Mockito.when(categoryService.addCategory(any())).thenReturn(response);
        mockMvc.perform(post("/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteCategory_success() throws Exception {
        mockMvc.perform(delete("/admin/categories/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(categoryService).deleteCategory(1L);
    }

    @Test
    void updateCategory_success() throws Exception {
        CategoryDto request = new CategoryDto();
        CategoryDto response = new CategoryDto();
        Mockito.when(categoryService.updateCategory(any(), any())).thenReturn(response);
        mockMvc.perform(patch("/admin/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
} 