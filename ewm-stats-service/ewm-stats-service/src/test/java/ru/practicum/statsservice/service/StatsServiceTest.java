package ru.practicum.statsservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.statsdto.EndpointHit;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.exception.BadRequestException;
import ru.practicum.statsservice.exception.StatsServiceException;
import ru.practicum.statsservice.model.EndpointHitEntity;
import ru.practicum.statsservice.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private EndpointHitRepository repository;

    @InjectMocks
    private StatsServiceImpl statsService;

    private EndpointHit validHit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        validHit = EndpointHit.builder()
                .app("test-app")
                .uri("/test")
                .ip("192.168.1.1")
                .timestamp("2023-01-01 12:00:00")
                .build();

        startDate = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        endDate = LocalDateTime.of(2023, 1, 2, 0, 0, 0);
    }

    @Test
    void saveHit_WithValidData_ShouldSaveEntity() {
        EndpointHitEntity expectedEntity = EndpointHitEntity.builder()
                .app("test-app")
                .uri("/test")
                .ip("192.168.1.1")
                .timestamp(LocalDateTime.of(2023, 1, 1, 12, 0, 0))
                .build();

        when(repository.save(any(EndpointHitEntity.class))).thenReturn(expectedEntity);

        statsService.saveHit(validHit);

        verify(repository, times(1)).save(any(EndpointHitEntity.class));
    }

    @Test
    void saveHit_WithInvalidTimestamp_ShouldThrowBadRequestException() {
        EndpointHit invalidHit = EndpointHit.builder()
                .app("test-app")
                .uri("/test")
                .ip("192.168.1.1")
                .timestamp("invalid-timestamp")
                .build();

        assertThrows(BadRequestException.class, () -> statsService.saveHit(invalidHit));
        verify(repository, never()).save(any(EndpointHitEntity.class));
    }

    @Test
    void getStats_WithValidData_ShouldReturnStats() {
        List<String> uris = Arrays.asList("/test", "/test2");
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{"test-app", "/test", 10L});
        mockResults.add(new Object[]{"test-app", "/test2", 5L});

        when(repository.getStats(any(LocalDateTime.class), any(LocalDateTime.class), eq(uris)))
                .thenReturn(mockResults);

        List<ViewStats> result = statsService.getStats(startDate, endDate, uris, false);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test-app", result.get(0).getApp());
        assertEquals("/test", result.get(0).getUri());
        assertEquals(10L, result.get(0).getHits());
        assertEquals("test-app", result.get(1).getApp());
        assertEquals("/test2", result.get(1).getUri());
        assertEquals(5L, result.get(1).getHits());

        verify(repository, times(1)).getStats(startDate, endDate, uris);
        verify(repository, never()).getStatsUnique(any(), any(), any());
    }

    @Test
    void getStats_WithUniqueFlag_ShouldCallUniqueMethod() {
        List<String> uris = Arrays.asList("/test");
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{"test-app", "/test", 5L});

        when(repository.getStatsUnique(any(LocalDateTime.class), any(LocalDateTime.class), eq(uris)))
                .thenReturn(mockResults);

        List<ViewStats> result = statsService.getStats(startDate, endDate, uris, true);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getHits());

        verify(repository, times(1)).getStatsUnique(startDate, endDate, uris);
        verify(repository, never()).getStats(any(), any(), any());
    }

    @Test
    void getStats_WithNullUris_ShouldPassNullToRepository() {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{"test-app", "/test", 10L});

        when(repository.getStats(any(LocalDateTime.class), any(LocalDateTime.class), isNull()))
                .thenReturn(mockResults);

        List<ViewStats> result = statsService.getStats(startDate, endDate, null, false);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(repository, times(1)).getStats(startDate, endDate, null);
    }

    @Test
    void getStats_WithInvalidDateRange_ShouldThrowBadRequestException() {
        assertThrows(BadRequestException.class, () -> 
                statsService.getStats(endDate, startDate, Arrays.asList("/test"), false));
        
        verify(repository, never()).getStats(any(), any(), any());
        verify(repository, never()).getStatsUnique(any(), any(), any());
    }

    @Test
    void getStats_WithNullDates_ShouldThrowBadRequestException() {
        assertThrows(BadRequestException.class, () -> 
                statsService.getStats(null, endDate, Arrays.asList("/test"), false));
        
        assertThrows(BadRequestException.class, () -> 
                statsService.getStats(startDate, null, Arrays.asList("/test"), false));
        
        verify(repository, never()).getStats(any(), any(), any());
        verify(repository, never()).getStatsUnique(any(), any(), any());
    }

    @Test
    void validateDateRange_WithValidRange_ShouldNotThrowException() {
        assertDoesNotThrow(() -> statsService.validateDateRange(startDate, endDate));
    }

    @Test
    void validateDateRange_WithInvalidRange_ShouldThrowBadRequestException() {
        assertThrows(BadRequestException.class, () -> 
                statsService.validateDateRange(endDate, startDate));
    }

    @Test
    void validateDateRange_WithNullDates_ShouldThrowBadRequestException() {
        assertThrows(BadRequestException.class, () -> 
                statsService.validateDateRange(null, endDate));
        
        assertThrows(BadRequestException.class, () -> 
                statsService.validateDateRange(startDate, null));
    }

    @Test
    void decodeUris_WithValidUris_ShouldReturnDecodedUris() {
        List<String> encodedUris = Arrays.asList(
                "http%3A//example.com/test",
                "http%3A//example.com/test2"
        );

        List<String> result = statsService.decodeUris(encodedUris);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("http://example.com/test", result.get(0));
        assertEquals("http://example.com/test2", result.get(1));
    }

    @Test
    void decodeUris_WithNullUris_ShouldReturnNull() {
        List<String> result = statsService.decodeUris(null);

        assertNull(result);
    }

    @Test
    void decodeUris_WithEmptyUris_ShouldReturnNull() {
        List<String> result = statsService.decodeUris(new ArrayList<>());

        assertNull(result);
    }

    @Test
    void decodeUris_WithInvalidEncoding_ShouldThrowBadRequestException() {
        List<String> invalidUris = Arrays.asList("%invalid%");

        assertThrows(BadRequestException.class, () -> statsService.decodeUris(invalidUris));
    }

    @Test
    void handleHit_WithValidData_ShouldCallSaveHit() {
        when(repository.save(any(EndpointHitEntity.class))).thenReturn(new EndpointHitEntity());

        statsService.handleHit(validHit);

        verify(repository, times(1)).save(any(EndpointHitEntity.class));
    }

    @Test
    void handleHit_WithRepositoryException_ShouldThrowStatsServiceException() {
        when(repository.save(any(EndpointHitEntity.class))).thenThrow(new RuntimeException("DB Error"));

        assertThrows(StatsServiceException.class, () -> statsService.handleHit(validHit));
    }

    @Test
    void handleGetStats_WithValidData_ShouldReturnStats() {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{"test-app", "/test", 10L});

        when(repository.getStats(any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenReturn(mockResults);

        List<ViewStats> result = statsService.handleGetStats(
                "2023-01-01 00:00:00", 
                "2023-01-02 00:00:00", 
                Arrays.asList("/test"), 
                false
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getHits());
    }

    @Test
    void handleGetStats_WithInvalidDateFormat_ShouldThrowBadRequestException() {
        assertThrows(BadRequestException.class, () -> 
                statsService.handleGetStats("invalid-date", "2023-01-02 00:00:00", Arrays.asList("/test"), false));
    }

    @Test
    void handleGetStats_WithRepositoryException_ShouldThrowStatsServiceException() {
        when(repository.getStats(any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenThrow(new RuntimeException("DB Error"));

        assertThrows(StatsServiceException.class, () -> 
                statsService.handleGetStats("2023-01-01 00:00:00", "2023-01-02 00:00:00", Arrays.asList("/test"), false));
    }
} 