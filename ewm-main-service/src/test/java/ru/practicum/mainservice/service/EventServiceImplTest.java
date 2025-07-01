package ru.practicum.mainservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.service.impl.EventServiceImpl;
import ru.practicum.mainservice.dto.response.EventFullDto;
import ru.practicum.mainservice.exception.NotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventServiceImpl eventService;
    private Event event;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setTitle("event1");
    }

    @Test
    void getEventById_ShouldReturnEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        assertDoesNotThrow(() -> eventService.getEventPublic(1L));
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void getEventById_NotFound_ShouldThrowException() {
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventService.getEventPublic(2L));
        verify(eventRepository, times(1)).findById(2L);
    }
} 