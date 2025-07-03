package ru.practicum.mainservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.impl.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
        when(userRepository.findAllById(java.util.Collections.singletonList(1L))).thenReturn(java.util.Collections.singletonList(user));
        assertDoesNotThrow(() -> userService.getUsers(java.util.Collections.singletonList(1L), 0, 1));
        verify(userRepository, times(1)).findAllById(java.util.Collections.singletonList(1L));
    }

    @Test
    void getUserById_NotFound_ShouldReturnEmptyList() {
        when(userRepository.findAllById(java.util.Collections.singletonList(2L))).thenReturn(java.util.Collections.emptyList());
        assertDoesNotThrow(() -> userService.getUsers(java.util.Collections.singletonList(2L), 0, 1));
        verify(userRepository, times(1)).findAllById(java.util.Collections.singletonList(2L));
    }
}