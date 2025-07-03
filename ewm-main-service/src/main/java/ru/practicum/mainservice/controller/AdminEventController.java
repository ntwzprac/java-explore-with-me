package ru.practicum.mainservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.UpdateEventAdminRequest;
import ru.practicum.mainservice.dto.response.EventFullDto;
import ru.practicum.mainservice.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Getting events with parameters: users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        List<EventFullDto> events = eventService.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("Found {} events", events.size());
        return events;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId, @RequestBody @Valid UpdateEventAdminRequest dto) {
        log.info("Updating event with id: {}, status: {}", eventId, dto.getStateAction());
        EventFullDto result = eventService.updateEventAdmin(eventId, dto);
        log.info("Event with id: {} successfully updated", eventId);
        return result;
    }
}