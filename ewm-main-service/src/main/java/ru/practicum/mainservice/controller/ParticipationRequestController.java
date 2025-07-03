package ru.practicum.mainservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.response.ParticipationRequestDto;
import ru.practicum.mainservice.service.ParticipationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class ParticipationRequestController {
    private final ParticipationRequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        log.info("Getting requests for user id: {}", userId);
        List<ParticipationRequestDto> requests = requestService.getUserRequests(userId);
        log.info("Found {} requests", requests.size());
        return requests;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipationRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Creating participation request for event id: {} from user id: {}", eventId, userId);
        ParticipationRequestDto request = requestService.addParticipationRequest(userId, eventId);
        log.info("Participation request successfully created with id: {}", request.getId());
        return request;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Canceling request id: {} by user id: {}", requestId, userId);
        ParticipationRequestDto request = requestService.cancelRequest(userId, requestId);
        log.info("Request id: {} successfully canceled", requestId);
        return request;
    }
}