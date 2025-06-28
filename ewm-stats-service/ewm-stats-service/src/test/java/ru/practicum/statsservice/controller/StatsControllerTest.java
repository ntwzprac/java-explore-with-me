package ru.practicum.statsservice.controller;

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
import ru.practicum.statsdto.EndpointHit;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.service.StatsService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {

    @Mock
    private StatsService statsService;

    @InjectMocks
    private StatsController statsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(statsController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void hit_ShouldReturnCreatedStatus() throws Exception {
        EndpointHit hit = EndpointHit.builder()
                .app("test-app")
                .uri("/test")
                .ip("192.168.1.1")
                .timestamp("2023-01-01 12:00:00")
                .build();

        doNothing().when(statsService).handleHit(any(EndpointHit.class));

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hit)))
                .andExpect(status().isCreated());

        verify(statsService, times(1)).handleHit(any(EndpointHit.class));
    }

    @Test
    void hit_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        EndpointHit invalidHit = EndpointHit.builder()
                .app("")
                .uri("")
                .ip("")
                .timestamp("")
                .build();

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidHit)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStats_ShouldReturnStatsList() throws Exception {
        List<ViewStats> expectedStats = Arrays.asList(
                ViewStats.builder()
                        .app("test-app")
                        .uri("/test")
                        .hits(10L)
                        .build(),
                ViewStats.builder()
                        .app("test-app")
                        .uri("/test2")
                        .hits(5L)
                        .build()
        );

        when(statsService.handleGetStats(anyString(), anyString(), anyList(), anyBoolean()))
                .thenReturn(expectedStats);

        mockMvc.perform(get("/stats")
                        .param("start", "2023-01-01 00:00:00")
                        .param("end", "2023-01-02 00:00:00")
                        .param("uris", "/test", "/test2")
                        .param("unique", "false"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].app").value("test-app"))
                .andExpect(jsonPath("$[0].uri").value("/test"))
                .andExpect(jsonPath("$[0].hits").value(10))
                .andExpect(jsonPath("$[1].app").value("test-app"))
                .andExpect(jsonPath("$[1].uri").value("/test2"))
                .andExpect(jsonPath("$[1].hits").value(5));

        verify(statsService, times(1)).handleGetStats(
                eq("2023-01-01 00:00:00"),
                eq("2023-01-02 00:00:00"),
                eq(Arrays.asList("/test", "/test2")),
                eq(false)
        );
    }

    @Test
    void getStats_WithoutUris_ShouldReturnStatsForAllUris() throws Exception {
        List<ViewStats> expectedStats = Arrays.asList(
                ViewStats.builder()
                        .app("test-app")
                        .uri("/test")
                        .hits(10L)
                        .build()
        );

        when(statsService.handleGetStats(anyString(), anyString(), isNull(), anyBoolean()))
                .thenReturn(expectedStats);

        mockMvc.perform(get("/stats")
                        .param("start", "2023-01-01 00:00:00")
                        .param("end", "2023-01-02 00:00:00")
                        .param("unique", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value("test-app"))
                .andExpect(jsonPath("$[0].uri").value("/test"))
                .andExpect(jsonPath("$[0].hits").value(10));

        verify(statsService, times(1)).handleGetStats(
                eq("2023-01-01 00:00:00"),
                eq("2023-01-02 00:00:00"),
                isNull(),
                eq(true)
        );
    }

    @Test
    void getStats_WithMissingRequiredParams_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "2023-01-01 00:00:00"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/stats")
                        .param("end", "2023-01-02 00:00:00"))
                .andExpect(status().isBadRequest());
    }
}