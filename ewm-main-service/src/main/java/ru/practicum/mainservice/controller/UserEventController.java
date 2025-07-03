package ru.practicum.mainservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.NewEventDto;
import ru.practicum.mainservice.dto.request.UpdateEventUserRequest;
import ru.practicum.mainservice.dto.response.EventFullDto;
import ru.practicum.mainservice.dto.response.EventShortDto;
import ru.practicum.mainservice.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class UserEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("Getting events for user id: {}, from={}, size={}", userId, from, size);
        List<EventShortDto> events = eventService.getUserEvents(userId, from, size);
        log.info("Found {} events", events.size());
        return events;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto dto) {
        log.info("Creating new event by user id: {}, event title: {}", userId, dto.getTitle());
        EventFullDto event = eventService.addEvent(userId, dto);
        log.info("Event successfully created with id: {}", event.getId());
        return event;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Getting event id: {} for user id: {}", eventId, userId);
        EventFullDto event = eventService.getUserEvent(userId, eventId);
        log.info("Event found: {}", event.getTitle());
        return event;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody @Valid UpdateEventUserRequest dto) {
        log.info("Updating event id: {} by user id: {}, state action: {}", eventId, userId, dto.getStateAction());
        EventFullDto event = eventService.updateUserEvent(userId, eventId, dto);
        log.info("Event id: {} successfully updated", eventId);
        return event;
    }
}