package ru.practicum.statsservice.service;

import ru.practicum.statsdto.EndpointHit;
import ru.practicum.statsdto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void saveHit(EndpointHit hit);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

    void validateDateRange(LocalDateTime start, LocalDateTime end);

    List<String> decodeUris(List<String> uris);

    void handleHit(EndpointHit hit);

    List<ViewStats> handleGetStats(String start, String end, List<String> uris, boolean unique);
}
