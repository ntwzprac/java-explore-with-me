package ru.practicum.statsservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.statsservice.model.EndpointHitEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class EndpointHitRepositoryTest {

    @Autowired
    private EndpointHitRepository repository;

    private EndpointHitEntity hit1;
    private EndpointHitEntity hit2;
    private EndpointHitEntity hit3;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        endDate = LocalDateTime.of(2023, 1, 2, 0, 0, 0);

        hit1 = EndpointHitEntity.builder()
                .app("test-app")
                .uri("/test1")
                .ip("192.168.1.1")
                .timestamp(LocalDateTime.of(2023, 1, 1, 12, 0, 0))
                .build();

        hit2 = EndpointHitEntity.builder()
                .app("test-app")
                .uri("/test1")
                .ip("192.168.1.2")
                .timestamp(LocalDateTime.of(2023, 1, 1, 13, 0, 0))
                .build();

        hit3 = EndpointHitEntity.builder()
                .app("test-app")
                .uri("/test2")
                .ip("192.168.1.1")
                .timestamp(LocalDateTime.of(2023, 1, 1, 14, 0, 0))
                .build();

        repository.saveAll(Arrays.asList(hit1, hit2, hit3));
    }

    @Test
    void save_ShouldSaveEntity() {
        EndpointHitEntity newHit = EndpointHitEntity.builder()
                .app("new-app")
                .uri("/new")
                .ip("192.168.1.3")
                .timestamp(LocalDateTime.now())
                .build();

        EndpointHitEntity saved = repository.save(newHit);

        assertNotNull(saved.getId());
        assertEquals("new-app", saved.getApp());
        assertEquals("/new", saved.getUri());
        assertEquals("192.168.1.3", saved.getIp());
    }

    @Test
    void getStats_ShouldReturnCorrectStats() {
        List<String> uris = Arrays.asList("/test1", "/test2");

        List<Object[]> results = repository.getStats(startDate, endDate, uris);

        assertEquals(2, results.size());

        assertEquals("test-app", results.get(0)[0]);
        assertEquals("/test1", results.get(0)[1]);
        assertEquals(2L, results.get(0)[2]);

        assertEquals("test-app", results.get(1)[0]);
        assertEquals("/test2", results.get(1)[1]);
        assertEquals(1L, results.get(1)[2]);
    }

    @Test
    void getStatsUnique_ShouldReturnCorrectUniqueStats() {
        List<String> uris = Arrays.asList("/test1", "/test2");

        List<Object[]> results = repository.getStatsUnique(startDate, endDate, uris);

        assertEquals(2, results.size());

        assertEquals("test-app", results.get(0)[0]);
        assertEquals("/test1", results.get(0)[1]);
        assertEquals(2L, results.get(0)[2]);

        assertEquals("test-app", results.get(1)[0]);
        assertEquals("/test2", results.get(1)[1]);
        assertEquals(1L, results.get(1)[2]);
    }

    @Test
    void getStats_WithNullUris_ShouldReturnAllStats() {
        List<Object[]> results = repository.getStats(startDate, endDate, null);

        assertEquals(2, results.size());
    }

    @Test
    void getStats_WithEmptyUris_ShouldReturnAllStats() {
        List<Object[]> results = repository.getStats(startDate, endDate, null);

        assertEquals(2, results.size());
    }

    @Test
    void getStats_WithDateRangeOutsideData_ShouldReturnEmptyList() {
        LocalDateTime futureStart = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime futureEnd = LocalDateTime.of(2024, 1, 2, 0, 0, 0);

        List<Object[]> results = repository.getStats(futureStart, futureEnd, Arrays.asList("/test1"));

        assertTrue(results.isEmpty());
    }

    @Test
    void getStats_WithNonExistentUri_ShouldReturnEmptyList() {
        List<String> nonExistentUris = Arrays.asList("/non-existent");

        List<Object[]> results = repository.getStats(startDate, endDate, nonExistentUris);

        assertTrue(results.isEmpty());
    }
}