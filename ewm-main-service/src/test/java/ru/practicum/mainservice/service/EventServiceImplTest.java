package ru.practicum.mainservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.mainservice.dto.mapper.EventMapper;
import ru.practicum.mainservice.dto.request.NewEventDto;
import ru.practicum.mainservice.dto.request.UpdateEventAdminRequest;
import ru.practicum.mainservice.dto.request.UpdateEventUserRequest;
import ru.practicum.mainservice.dto.response.EventFullDto;
import ru.practicum.mainservice.dto.response.EventShortDto;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.*;
import ru.practicum.mainservice.repository.CategoryRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.impl.EventServiceImpl;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.EndpointHit;
import ru.practicum.statsdto.ViewStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private StatsClient statsClient;
    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserEvent_NotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventService.getUserEvent(1L, 1L));
    }

    @Test
    void testAddEvent_UserNotFound() {
        NewEventDto dto = mock(NewEventDto.class);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventService.addEvent(1L, dto));
    }

    @Test
    void testGetEventsAdmin_Empty() {
        when(eventRepository.searchEventsAdmin(any(), any(), any(), any(), any(), any())).thenReturn(Page.empty());
        List<EventFullDto> result = eventService.getEventsAdmin(null, null, null, null, null, 0, 10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateEventAdmin_EventNotFound() {
        UpdateEventAdminRequest dto = mock(UpdateEventAdminRequest.class);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventService.updateEventAdmin(1L, dto));
    }

    @Test
    void testUpdateUserEvent_EventNotFound() {
        UpdateEventUserRequest dto = mock(UpdateEventUserRequest.class);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventService.updateUserEvent(1L, 1L, dto));
    }

    @Test
    void testUpdateUserEvent_NotInitiator() {
        UpdateEventUserRequest dto = mock(UpdateEventUserRequest.class);
        Event event = mock(Event.class);
        User user = mock(User.class);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(user);
        when(user.getId()).thenReturn(2L);
        assertThrows(NotFoundException.class, () -> eventService.updateUserEvent(1L, 1L, dto));
    }

    @Test
    void testGetEventPublic_EventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(httpServletRequest.getRequestURI()).thenReturn("/events/1");
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        assertThrows(NotFoundException.class, () -> eventService.getEventPublic(1L));
    }

    @Test
    void testGetEventPublic_NotPublished() {
        Event event = mock(Event.class);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getState()).thenReturn(EventState.PENDING);
        when(httpServletRequest.getRequestURI()).thenReturn("/events/1");
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        assertThrows(NotFoundException.class, () -> eventService.getEventPublic(1L));
    }

    @Test
    void testGetEventsPublic_Empty() {
        when(eventRepository.searchEventsPublic(any(), any(), any(), any(), any(), any())).thenReturn(Page.empty());
        when(httpServletRequest.getRequestURI()).thenReturn("/events");
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        List<EventShortDto> result = eventService.getEventsPublic(null, null, null, null, null, null, null, 0, 10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
} 