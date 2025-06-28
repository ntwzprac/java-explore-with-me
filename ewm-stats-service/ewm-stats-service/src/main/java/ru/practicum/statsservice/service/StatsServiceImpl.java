package ru.practicum.statsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statsdto.EndpointHit;
import ru.practicum.statsdto.EndpointHitResponse;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.model.EndpointHitEntity;
import ru.practicum.statsservice.repository.EndpointHitRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class StatsServiceImpl implements StatsService {
    private final EndpointHitRepository repository;

    @Override
    public EndpointHitResponse saveHit(EndpointHit hit) {
        EndpointHitEntity entity = EndpointHitEntity.builder()
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();
        EndpointHitEntity saved = repository.save(entity);

        return EndpointHitResponse.builder()
                .id(saved.getId())
                .app(saved.getApp())
                .uri(saved.getUri())
                .ip(saved.getIp())
                .timestamp(saved.getTimestamp())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        validateDateRange(start, end);

        List<String> decodedUris = decodeUris(uris);

        List<Object[]> results = unique ?
                repository.getStatsUnique(start, end, decodedUris) :
                repository.getStats(start, end, decodedUris);
        List<ViewStats> stats = new ArrayList<>();
        for (Object[] row : results) {
            stats.add(ViewStats.builder()
                    .app(Objects.toString(row[0]))
                    .uri(Objects.toString(row[1]))
                    .hits(Long.parseLong(row[2].toString()))
                    .build());
        }
        return stats;
    }

    @Override
    public void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }

    @Override
    public List<String> decodeUris(List<String> uris) {
        if (uris == null) {
            return null;
        }
        return uris.stream()
                .map(uri -> URLDecoder.decode(uri, StandardCharsets.UTF_8))
                .toList();
    }
}
