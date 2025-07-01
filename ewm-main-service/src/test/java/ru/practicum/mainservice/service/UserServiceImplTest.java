package ru.practicum.mainservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("user1");
        user.setEmail("user1@email.com");
    }

    @Test
    void getUserById_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertDoesNotThrow(() -> userService.getUsers(java.util.Collections.singletonList(1L), 0, 1));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_NotFound_ShouldThrowException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUsers(java.util.Collections.singletonList(2L), 0, 1));
        verify(userRepository, times(1)).findById(2L);
    }
} 