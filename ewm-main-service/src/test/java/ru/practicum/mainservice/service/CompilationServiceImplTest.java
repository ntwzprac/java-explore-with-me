package ru.practicum.mainservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.Compilation;
import ru.practicum.mainservice.repository.CompilationRepository;
import ru.practicum.mainservice.service.impl.CompilationServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompilationServiceImplTest {
    @Mock
    private CompilationRepository compilationRepository;
    @InjectMocks
    private CompilationServiceImpl compilationService;
    private Compilation compilation;

    @BeforeEach
    void setUp() {
        compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("compilation1");
    }

    @Test
    void getCompilationById_ShouldReturnCompilation() {
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));
        assertDoesNotThrow(() -> compilationService.getCompilation(1L));
        verify(compilationRepository, times(1)).findById(1L);
    }

    @Test
    void getCompilationById_NotFound_ShouldThrowException() {
        when(compilationRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> compilationService.getCompilation(2L));
        verify(compilationRepository, times(1)).findById(2L);
    }
}