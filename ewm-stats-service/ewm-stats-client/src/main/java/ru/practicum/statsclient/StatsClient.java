package ru.practicum.statsclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statsdto.EndpointHit;
import ru.practicum.statsdto.ViewStats;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClient {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public StatsClient(@Value("${stats-service.url:http://localhost:9090}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplateBuilder().build();
    }

    public void saveHit(EndpointHit hit) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHit> request = new HttpEntity<>(hit, headers);
        restTemplate.postForEntity(baseUrl + "/hit", request, Void.class);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        StringBuilder url = new StringBuilder(baseUrl + "/stats?");

        try {
            url.append("start=").append(URLEncoder.encode(start.format(FORMATTER), StandardCharsets.UTF_8));
            url.append("&end=").append(URLEncoder.encode(end.format(FORMATTER), StandardCharsets.UTF_8));

            if (uris != null && !uris.isEmpty()) {
                for (String uri : uris) {
                    url.append("&uris=").append(URLEncoder.encode(uri, StandardCharsets.UTF_8));
                }
            }

            if (unique) {
                url.append("&unique=true");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error encoding URL parameters", e);
        }

        ResponseEntity<ViewStats[]> response = restTemplate.getForEntity(url.toString(), ViewStats[].class);
        return List.of(response.getBody());
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end) {
        return getStats(start, end, null, false);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return getStats(start, end, uris, false);
    }

    public List<ViewStats> getStatsUnique(LocalDateTime start, LocalDateTime end) {
        return getStats(start, end, null, true);
    }

    public List<ViewStats> getStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return getStats(start, end, uris, true);
    }
}
