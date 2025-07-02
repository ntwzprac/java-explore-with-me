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
import ru.practicum.mainservice.dto.response.EventShortDto;
import ru.practicum.mainservice.service.EventService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventController eventController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getEventsPublic_ShouldReturnList() throws Exception {
        List<EventShortDto> events = Arrays.asList(
                EventShortDto.builder().id(1L).title("event1").build(),
                EventShortDto.builder().id(2L).title("event2").build()
        );
        when(eventService.getEventsPublic(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(events);
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("event1"));
        verify(eventService, times(1)).getEventsPublic(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void getEventById_ShouldReturnEventFullDto() throws Exception {
        var eventFullDto = ru.practicum.mainservice.dto.response.EventFullDto.builder()
                .id(1L)
                .title("event1")
                .annotation("annotation")
                .description("desc")
                .category(ru.practicum.mainservice.dto.response.CategoryDto.builder().id(1L).name("cat").build())
                .initiator(ru.practicum.mainservice.dto.response.UserShortDto.builder().id(1L).name("user").build())
                .location(ru.practicum.mainservice.model.Location.builder().lat(10.0f).lon(20.0f).build())
                .paid(false)
                .participantLimit(100)
                .requestModeration(false)
                .eventDate("2024-01-01 10:00:00")
                .createdOn("2023-12-01 10:00:00")
                .publishedOn("2023-12-02 10:00:00")
                .state("PUBLISHED")
                .confirmedRequests(5)
                .views(10)
                .build();
        when(eventService.getEventPublic(1L)).thenReturn(eventFullDto);
        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("event1"))
                .andExpect(jsonPath("$.category.id").value(1L))
                .andExpect(jsonPath("$.initiator.name").value("user"));
        verify(eventService, times(1)).getEventPublic(1L);
    }
}