package ru.practicum.mainservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.response.EventFullDto;
import ru.practicum.mainservice.dto.response.EventShortDto;
import ru.practicum.mainservice.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Getting events with parameters: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, " +
                "onlyAvailable={}, sort={}, from={}, size={}", 
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        List<EventShortDto> events = eventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.info("Found {} events", events.size());
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id) {
        log.info("Getting event with id: {}", id);
        EventFullDto event = eventService.getEventPublic(id);
        log.info("Event found: {}", event.getTitle());
        return event;
    }
}