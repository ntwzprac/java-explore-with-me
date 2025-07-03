package ru.practicum.mainservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.dto.response.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.dto.response.ParticipationRequestDto;
import ru.practicum.mainservice.service.ParticipationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
public class EventParticipationRequestController {
    private final ParticipationRequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getEventParticipants(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Getting participation requests for event id: {}, user id: {}", eventId, userId);
        List<ParticipationRequestDto> requests = requestService.getEventParticipants(userId, eventId);
        log.info("Found {} participation requests", requests.size());
        return requests;
    }

    @PatchMapping
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Updating participation request status for event id: {}, user id: {}, new status: {}",
                eventId, userId, request.getStatus());
        EventRequestStatusUpdateResult result = requestService.changeRequestStatus(userId, eventId, request);
        log.info("Updated request statuses: confirmed - {}, rejected - {}",
                result.getConfirmedRequests().size(), result.getRejectedRequests().size());
        return result;
    }
}