package ru.practicum.mainservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.dto.response.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.dto.response.ParticipationRequestDto;
import ru.practicum.mainservice.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
public class EventParticipationRequestController {
    private final ParticipationRequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getEventParticipants(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getEventParticipants(userId, eventId);
    }

    @PatchMapping
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest request) {
        return requestService.changeRequestStatus(userId, eventId, request);
    }
}