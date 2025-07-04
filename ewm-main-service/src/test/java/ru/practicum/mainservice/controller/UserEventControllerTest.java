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
import ru.practicum.mainservice.dto.request.NewEventDto;
import ru.practicum.mainservice.dto.response.CategoryDto;
import ru.practicum.mainservice.dto.response.EventFullDto;
import ru.practicum.mainservice.dto.response.EventShortDto;
import ru.practicum.mainservice.dto.response.UserShortDto;
import ru.practicum.mainservice.model.Location;
import ru.practicum.mainservice.service.EventService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserEventControllerTest {
    @Mock
    private EventService eventService;
    @InjectMocks
    private UserEventController userEventController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userEventController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getUserEvents_ShouldReturnList() throws Exception {
        List<EventShortDto> events = Arrays.asList(
                EventShortDto.builder().id(1L).title("event1").build(),
                EventShortDto.builder().id(2L).title("event2").build()
        );
        when(eventService.getUserEvents(anyLong(), anyInt(), anyInt())).thenReturn(events);
        mockMvc.perform(get("/users/1/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("event1"));
        verify(eventService, times(1)).getUserEvents(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getUserEventById_ShouldReturnEventFullDto() throws Exception {
        var eventFullDto = EventFullDto.builder()
                .id(1L)
                .title("event1")
                .build();
        when(eventService.getUserEvent(1L, 2L)).thenReturn(eventFullDto);
        mockMvc.perform(get("/users/1/events/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("event1"));
        verify(eventService, times(1)).getUserEvent(1L, 2L);
    }

    @Test
    void addEvent_ShouldReturnCreatedEventFullDto() throws Exception {
        var newEvent = NewEventDto.builder()
                .title("event1")
                .annotation("Some valid annotation for the event.")
                .description("Some valid description for the event, at least 20 characters long.")
                .eventDate("2030-01-01T12:00:00")
                .location(Location.builder().lat(10.0f).lon(20.0f).build())
                .category(1L)
                .build();
        var eventFullDto = EventFullDto.builder()
                .id(1L)
                .title("event1")
                .annotation("Some valid annotation for the event.")
                .description("Some valid description for the event, at least 20 characters long.")
                .eventDate("2030-01-01T12:00:00")
                .location(Location.builder().lat(10.0f).lon(20.0f).build())
                .category(CategoryDto.builder().id(1L).name("Category1").build())
                .initiator(UserShortDto.builder().id(1L).name("User1").build())
                .paid(false)
                .participantLimit(0)
                .requestModeration(true)
                .build();
        when(eventService.addEvent(eq(1L), any())).thenReturn(eventFullDto);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEvent)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("event1"));
        verify(eventService, times(1)).addEvent(eq(1L), any());
    }
}