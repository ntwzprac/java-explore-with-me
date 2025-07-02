package ru.practicum.mainservice.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import ru.practicum.mainservice.dto.request.NewEventDto;
import ru.practicum.mainservice.dto.request.UpdateEventAdminRequest;
import ru.practicum.mainservice.dto.request.UpdateEventUserRequest;
import ru.practicum.mainservice.dto.response.EventFullDto;
import ru.practicum.mainservice.dto.response.EventShortDto;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.InvalidDateException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.CategoryRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.impl.EventServiceImpl;
import ru.practicum.statsclient.StatsClient;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    void testAddEvent_Success() {
        NewEventDto dto = mock(NewEventDto.class);
        when(dto.getCategory()).thenReturn(1L);
        when(dto.getEventDate()).thenReturn(java.time.LocalDateTime.now().plusHours(3).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        User user = mock(User.class);
        Category category = mock(Category.class);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(eventRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        assertDoesNotThrow(() -> eventService.addEvent(1L, dto));
    }

    @Test
    void testAddEvent_CategoryNotFound() {
        NewEventDto dto = mock(NewEventDto.class);
        when(dto.getCategory()).thenReturn(1L);
        when(dto.getEventDate()).thenReturn(java.time.LocalDateTime.now().plusHours(3).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        User user = mock(User.class);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventService.addEvent(1L, dto));
    }

    @Test
    void testAddEvent_ConflictDate() {
        NewEventDto dto = mock(NewEventDto.class);
        when(dto.getCategory()).thenReturn(1L);
        when(dto.getEventDate()).thenReturn(java.time.LocalDateTime.now().plusMinutes(30).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        User user = mock(User.class);
        Category category = mock(Category.class);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        assertThrows(InvalidDateException.class, () -> eventService.addEvent(1L, dto));
    }

    @Test
    void testUpdateEventAdmin_ConflictDate() {
        UpdateEventAdminRequest dto = mock(UpdateEventAdminRequest.class);
        Event event = mock(Event.class);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(dto.getEventDate()).thenReturn(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        assertThrows(InvalidDateException.class, () -> eventService.updateEventAdmin(1L, dto));
    }

    @Test
    void testUpdateEventAdmin_CategoryNotFound() {
        UpdateEventAdminRequest dto = mock(UpdateEventAdminRequest.class);
        Event event = mock(Event.class);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(dto.getEventDate()).thenReturn(null);
        when(dto.getCategory()).thenReturn(2L);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventService.updateEventAdmin(1L, dto));
    }

    @Test
    void testUpdateUserEvent_ConflictState() {
        UpdateEventUserRequest dto = mock(UpdateEventUserRequest.class);
        Event event = mock(Event.class);
        User user = mock(User.class);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(event.getState()).thenReturn(EventState.PUBLISHED);
        assertThrows(ConflictException.class, () -> eventService.updateUserEvent(1L, 1L, dto));
    }

    @Test
    void testUpdateUserEvent_ConflictDate() {
        UpdateEventUserRequest dto = mock(UpdateEventUserRequest.class);
        Event event = mock(Event.class);
        User user = mock(User.class);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(event.getState()).thenReturn(EventState.PENDING);
        when(dto.getEventDate()).thenReturn(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        assertThrows(InvalidDateException.class, () -> eventService.updateUserEvent(1L, 1L, dto));
    }

    @Test
    void testUpdateUserEvent_CategoryNotFound() {
        UpdateEventUserRequest dto = mock(UpdateEventUserRequest.class);
        Event event = mock(Event.class);
        User user = mock(User.class);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(event.getState()).thenReturn(EventState.PENDING);
        when(dto.getEventDate()).thenReturn(null);
        when(dto.getCategory()).thenReturn(2L);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventService.updateUserEvent(1L, 1L, dto));
    }
}