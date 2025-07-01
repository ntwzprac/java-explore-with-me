package ru.practicum.mainservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.NewEventDto;
import ru.practicum.mainservice.dto.request.UpdateEventUserRequest;
import ru.practicum.mainservice.dto.response.EventFullDto;
import ru.practicum.mainservice.dto.response.EventShortDto;
import ru.practicum.mainservice.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class UserEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return eventService.getUserEvents(userId, from, size);
    }

    @PostMapping
    public EventFullDto addEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto dto) {
        return eventService.addEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody @Valid UpdateEventUserRequest dto) {
        return eventService.updateUserEvent(userId, eventId, dto);
    }
}