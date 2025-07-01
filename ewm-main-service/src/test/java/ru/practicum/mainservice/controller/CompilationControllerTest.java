package ru.practicum.mainservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.mainservice.dto.response.CompilationDto;
import ru.practicum.mainservice.service.CompilationService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CompilationControllerTest {
    @Mock
    private CompilationService compilationService;
    @InjectMocks
    private CompilationController compilationController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(compilationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getCompilations_ShouldReturnList() throws Exception {
        List<CompilationDto> compilations = Arrays.asList(
                CompilationDto.builder().id(1L).title("compilation1").build(),
                CompilationDto.builder().id(2L).title("compilation2").build()
        );
        when(compilationService.getCompilations(any(), anyInt(), anyInt())).thenReturn(compilations);
        mockMvc.perform(get("/compilations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("compilation1"));
        verify(compilationService, times(1)).getCompilations(any(), anyInt(), anyInt());
    }
} 