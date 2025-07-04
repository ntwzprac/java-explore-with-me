package ru.practicum.mainservice.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.mapper.EventMapper;
import ru.practicum.mainservice.dto.request.NewEventDto;
import ru.practicum.mainservice.dto.request.UpdateEventAdminRequest;
import ru.practicum.mainservice.dto.request.UpdateEventUserRequest;
import ru.practicum.mainservice.dto.response.EventFullDto;
import ru.practicum.mainservice.dto.response.EventShortDto;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.EventConflictException;
import ru.practicum.mainservice.exception.InvalidDateException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.*;
import ru.practicum.mainservice.repository.CategoryRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.EventService;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.EndpointHit;
import ru.practicum.statsdto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final HttpServletRequest httpServletRequest;
    @Value("${spring.application.name}")
    private String appName;

    private LocalDateTime parseDate(String dateStr) {
        return LocalDateTime.parse(dateStr, DATE_FORMATTER);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found: " + categoryId));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
    }

    private void validateEventDateForAdmin(String eventDate) {
        if (eventDate != null) {
            LocalDateTime date = parseDate(eventDate);
            if (date.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new InvalidDateException("Event date must be at least 1 hour in the future for admin update");
            }
        }
    }

    private void validateEventDateForUser(String eventDate) {
        if (eventDate != null) {
            LocalDateTime date = parseDate(eventDate);
            if (date.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new InvalidDateException("Event date must be at least 2 hours in the future");
            }
        }
    }

    private void saveStatsHit() {
        statsClient.saveHit(EndpointHit.builder()
                .app(appName)
                .uri(httpServletRequest.getRequestURI())
                .ip(httpServletRequest.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(DATE_FORMATTER))
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<EventState> stateEnums = (states != null && !states.isEmpty()) ? states.stream().map(EventState::valueOf).toList() : null;
        LocalDateTime start = (rangeStart != null) ? parseDate(rangeStart) : null;
        LocalDateTime end = (rangeEnd != null) ? parseDate(rangeEnd) : null;

        List<Long> safeUsers = (users == null || users.isEmpty()) ? null : users;
        List<EventState> safeStates = (stateEnums == null || stateEnums.isEmpty()) ? null : stateEnums;
        List<Long> safeCategories = (categories == null || categories.isEmpty()) ? null : categories;

        return eventRepository.searchEventsAdmin(safeUsers, safeStates, safeCategories, start, end, pageRequest)
                .stream().map(EventMapper::toFullDto).toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = getEventOrThrow(eventId);
        validateEventDateForAdmin(dto.getEventDate());
        Category category = (dto.getCategory() != null) ? getCategoryOrThrow(dto.getCategory()) : null;
        Location location = dto.getLocation();
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "PUBLISH_EVENT" -> {
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new EventConflictException("Event is already published");
                    }
                    if (event.getState() == EventState.CANCELED) {
                        throw new EventConflictException("Cannot publish canceled event");
                    }
                }
                case "REJECT_EVENT" -> {
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new EventConflictException("Cannot reject published event");
                    }
                }
            }
        }
        EventMapper.updateEntityByAdmin(event, dto, category, location);
        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.findByInitiatorId(userId, pageRequest)
                .stream().map(EventMapper::toShortDto).toList();
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        User user = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(dto.getCategory());
        LocalDateTime eventDate = parseDate(dto.getEventDate());
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidDateException("Event date must be at least 2 hours in the future");
        }
        Event event = EventMapper.toEntity(dto, user, category);
        event.setEventDate(eventDate);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setConfirmedRequests(0);
        event.setViews(0);
        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event does not belong to user: " + userId);
        }
        return EventMapper.toFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event does not belong to user: " + userId);
        }
        if (!(event.getState() == EventState.PENDING || event.getState() == EventState.CANCELED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        validateEventDateForUser(dto.getEventDate());
        Category category = (dto.getCategory() != null) ? getCategoryOrThrow(dto.getCategory()) : null;
        Location location = dto.getLocation();
        EventMapper.updateEntityByUser(event, dto, category, location);
        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        saveStatsHit();

        if ((text == null || text.isBlank()) &&
                (categories == null || categories.isEmpty()) &&
                paid == null &&
                rangeStart == null &&
                rangeEnd == null) {

            Page<Event> eventsPage = eventRepository.searchEventsPublic(pageRequest);
            List<Event> resultEvents = new ArrayList<>(eventsPage.getContent());

            if (onlyAvailable != null && onlyAvailable) {
                resultEvents = resultEvents.stream()
                        .filter(event -> event.getParticipantLimit() == 0 ||
                                event.getParticipantLimit() > event.getConfirmedRequests())
                        .collect(Collectors.toList());
            }

            return processEventsWithStats(resultEvents, sort);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = (rangeStart != null) ? parseDate(rangeStart) : now;
        LocalDateTime end = (rangeEnd != null) ? parseDate(rangeEnd) : LocalDateTime.of(3000, 1, 1, 0, 0);
        if (start.isAfter(end)) {
            throw new InvalidDateException("rangeStart can't be after rangeEnd");
        }

        String safeText = (text == null || text.isBlank()) ? null : text.toLowerCase();
        List<Long> safeCategories = (categories == null || categories.isEmpty()) ? null : categories;

        Page<Event> eventsPage = eventRepository.searchEventsPublic(safeText, safeCategories, paid, start, end, pageRequest);
        List<Event> resultEvents = new ArrayList<>(eventsPage.getContent());

        if (onlyAvailable != null && onlyAvailable) {
            resultEvents = resultEvents.stream()
                    .filter(event -> event.getParticipantLimit() == 0 ||
                            event.getParticipantLimit() > event.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        return processEventsWithStats(resultEvents, sort);
    }

    private List<EventShortDto> processEventsWithStats(List<Event> events, String sort) {
        LocalDateTime now = LocalDateTime.now();
        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        LocalDateTime statsStart = events.stream()
                .map(Event::getPublishedOn)
                .filter(java.util.Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(now.minusYears(1));

        List<ViewStats> stats = statsClient.getStats(statsStart, now, uris, true);

        events.forEach(event -> {
            String eventUri = "/events/" + event.getId();
            stats.stream()
                    .filter(s -> s.getUri().equals(eventUri))
                    .findFirst()
                    .ifPresent(viewStats -> event.setViews(viewStats.getHits().intValue()));
        });

        return sortEvents(events, sort).stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    private List<Event> sortEvents(List<Event> events, String sort) {
        if (sort != null) {
            switch (sort) {
                case "EVENT_DATE" -> events.sort(Comparator.comparing(Event::getEventDate));
                case "VIEWS" -> events.sort(Comparator.comparing(Event::getViews).reversed());
            }
        }
        return events;
    }

    @Override
    @Transactional
    public EventFullDto getEventPublic(Long eventId) {
        Event event = getEventOrThrow(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event is not published");
        }
        EventFullDto dto = EventMapper.toFullDto(event);
        LocalDateTime start = event.getPublishedOn() != null ? event.getPublishedOn() : LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now();
        List<ViewStats> stats = statsClient.getStats(start, end, List.of("/events/" + eventId), true);
        if (!stats.isEmpty()) {
            dto.setViews(stats.getFirst().getHits().intValue());
        }
        saveStatsHit();
        return dto;
    }
}