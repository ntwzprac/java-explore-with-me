package ru.practicum.mainservice.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.mapper.EventMapper;
import ru.practicum.mainservice.dto.request.NewEventDto;
import ru.practicum.mainservice.dto.request.UpdateEventAdminRequest;
import ru.practicum.mainservice.dto.request.UpdateEventUserRequest;
import ru.practicum.mainservice.dto.response.EventFullDto;
import ru.practicum.mainservice.dto.response.EventShortDto;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.CategoryRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.EventService;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.EndpointHit;
import ru.practicum.statsdto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final HttpServletRequest httpServletRequest;
    @Value("${spring.application.name}")
    private String appName;

    @Override
    public List<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, int from, int size) {
        return eventRepository.findAll(PageRequest.of(from / size, size)).stream()
                .filter(event -> users == null || users.isEmpty() || users.contains(event.getInitiator().getId()))
                .filter(event -> states == null || states.isEmpty() || states.contains(event.getState().name()))
                .filter(event -> categories == null || categories.isEmpty() || categories.contains(event.getCategory().getId()))
                .filter(event -> {
                    if (rangeStart != null) {
                        LocalDateTime start = LocalDateTime.parse(rangeStart, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        if (event.getEventDate().isBefore(start)) return false;
                    }
                    if (rangeEnd != null) {
                        LocalDateTime end = LocalDateTime.parse(rangeEnd, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        if (event.getEventDate().isAfter(end)) return false;
                    }
                    return true;
                })
                .map(EventMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
        if (dto.getEventDate() != null) {
            LocalDateTime eventDate = LocalDateTime.parse(dto.getEventDate(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ru.practicum.mainservice.exception.ConflictException("Event date must be at least 1 hour in the future for admin update");
            }
        }
        Category category = null;
        if (dto.getCategory() != null) {
            category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found: " + dto.getCategory()));
        }
        ru.practicum.mainservice.model.Location location = dto.getLocation();
        EventMapper.updateEntityByAdmin(event, dto, category, location);
        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        return eventRepository.findAll(PageRequest.of(from / size, size)).stream()
                .filter(event -> event.getInitiator().getId().equals(userId))
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found: " + dto.getCategory()));
        LocalDateTime eventDate = LocalDateTime.parse(dto.getEventDate(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ru.practicum.mainservice.exception.ConflictException("Event date must be at least 2 hours in the future");
        }
        Event event = EventMapper.toEntity(dto, user, category);
        event.setEventDate(eventDate);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(ru.practicum.mainservice.model.EventState.PENDING);
        event.setConfirmedRequests(0);
        event.setViews(0);
        Event saved = eventRepository.save(event);
        return EventMapper.toFullDto(saved);
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event does not belong to user: " + userId);
        }
        return EventMapper.toFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event does not belong to user: " + userId);
        }
        if (!(event.getState() == ru.practicum.mainservice.model.EventState.PENDING || event.getState() == ru.practicum.mainservice.model.EventState.CANCELED)) {
            throw new ru.practicum.mainservice.exception.ConflictException("Only pending or canceled events can be changed");
        }
        if (dto.getEventDate() != null) {
            LocalDateTime eventDate = LocalDateTime.parse(dto.getEventDate(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ru.practicum.mainservice.exception.ConflictException("Event date must be at least 2 hours in the future");
            }
        }
        Category category = null;
        if (dto.getCategory() != null) {
            category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found: " + dto.getCategory()));
        }
        ru.practicum.mainservice.model.Location location = dto.getLocation();
        EventMapper.updateEntityByUser(event, dto, category, location);
        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, int from, int size) {
        String uri = httpServletRequest.getRequestURI();
        String ip = httpServletRequest.getRemoteAddr();
        statsClient.saveHit(EndpointHit.builder()
                .app(appName)
                .uri(uri)
                .ip(ip)
                .timestamp(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build());
        List<Event> events = eventRepository.findAll(PageRequest.of(from / size, size)).stream()
                .filter(event -> event.getState() == ru.practicum.mainservice.model.EventState.PUBLISHED)
                .filter(event -> text == null || text.isBlank() ||
                        event.getAnnotation().toLowerCase().contains(text.toLowerCase()) ||
                        event.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(event -> categories == null || categories.isEmpty() || categories.contains(event.getCategory().getId()))
                .filter(event -> paid == null || event.getPaid().equals(paid))
                .filter(event -> {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : now;
                    LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
                    if (event.getEventDate().isBefore(start)) return false;
                    if (end != null && event.getEventDate().isAfter(end)) return false;
                    return true;
                })
                .filter(event -> !Boolean.TRUE.equals(onlyAvailable) || event.getParticipantLimit() == 0 || event.getConfirmedRequests() < event.getParticipantLimit())
                .sorted((e1, e2) -> {
                    if (sort == null || sort.equals("EVENT_DATE")) {
                        return e1.getEventDate().compareTo(e2.getEventDate());
                    } else if (sort.equals("VIEWS")) {
                        return Integer.compare(e2.getViews(), e1.getViews());
                    }
                    return 0;
                })
                .collect(Collectors.toList());
        List<String> uris = events.stream().map(e -> "/events/" + e.getId()).collect(Collectors.toList());
        LocalDateTime start = events.stream().map(Event::getPublishedOn).filter(java.util.Objects::nonNull).min(LocalDateTime::compareTo).orElse(LocalDateTime.now().minusYears(1));
        LocalDateTime end = LocalDateTime.now();
        List<ViewStats> stats = statsClient.getStats(start, end, uris, false);
        return events.stream().map(event -> {
            EventShortDto dto = EventMapper.toShortDto(event);
            String eventUri = "/events/" + event.getId();
            stats.stream().filter(s -> s.getUri().equals(eventUri)).findFirst().ifPresent(viewStats -> dto.setViews(viewStats.getHits().intValue()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto getEventPublic(Long eventId) {
        String uri = httpServletRequest.getRequestURI();
        String ip = httpServletRequest.getRemoteAddr();
        statsClient.saveHit(EndpointHit.builder()
                .app(appName)
                .uri(uri)
                .ip(ip)
                .timestamp(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build());
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
        if (event.getState() != ru.practicum.mainservice.model.EventState.PUBLISHED) {
            throw new NotFoundException("Event is not published");
        }
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
        EventFullDto dto = EventMapper.toFullDto(event);
        LocalDateTime start = event.getPublishedOn() != null ? event.getPublishedOn() : LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now();
        List<ViewStats> stats = statsClient.getStats(start, end, List.of("/events/" + eventId), false);
        if (!stats.isEmpty()) {
            dto.setViews(stats.get(0).getHits().intValue());
        }
        return dto;
    }
}