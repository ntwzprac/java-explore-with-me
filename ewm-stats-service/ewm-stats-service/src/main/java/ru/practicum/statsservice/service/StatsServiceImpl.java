package ru.practicum.statsservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statsdto.EndpointHit;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.exception.BadRequestException;
import ru.practicum.statsservice.exception.StatsServiceException;
import ru.practicum.statsservice.model.EndpointHitEntity;
import ru.practicum.statsservice.repository.EndpointHitRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class StatsServiceImpl implements StatsService {
    private final EndpointHitRepository repository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsServiceImpl(EndpointHitRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveHit(EndpointHit hit) {
        LocalDateTime timestamp = parseDateTime(hit.getTimestamp());
        EndpointHitEntity entity = EndpointHitEntity.builder()
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(timestamp)
                .build();
        repository.save(entity);
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
        if (start == null || end == null) {
            throw new BadRequestException("Start and end dates cannot be null");
        }
        if (start.isAfter(end)) {
            throw new BadRequestException("Start date must be before end date");
        }
    }

    @Override
    public List<String> decodeUris(List<String> uris) {
        if (uris == null || uris.isEmpty()) {
            return null;
        }
        return uris.stream()
                .map(uri -> {
                    try {
                        return URLDecoder.decode(uri, StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        throw new BadRequestException("Invalid URI encoding: " + uri);
                    }
                })
                .toList();
    }

    private LocalDateTime parseDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, FORMATTER);
        } catch (Exception e) {
            throw new BadRequestException("Invalid timestamp format. Expected format: yyyy-MM-dd HH:mm:ss");
        }
    }

    @Override
    public void handleHit(EndpointHit hit) {
        try {
            saveHit(hit);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new StatsServiceException("Internal error while saving hit", e);
        }
    }

    @Override
    public List<ViewStats> handleGetStats(String start, String end, List<String> uris, boolean unique) {
        try {
            LocalDateTime startDate = parseAndDecodeDateTime(start);
            LocalDateTime endDate = parseAndDecodeDateTime(end);
            return getStats(startDate, endDate, uris, unique);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new StatsServiceException("Internal error while getting stats", e);
        }
    }

    private LocalDateTime parseAndDecodeDateTime(String dateTime) {
        try {
            String decoded = URLDecoder.decode(dateTime, StandardCharsets.UTF_8);
            return LocalDateTime.parse(decoded, FORMATTER);
        } catch (Exception e) {
            throw new BadRequestException("Invalid date format. Expected format: yyyy-MM-dd HH:mm:ss");
        }
    }
}
