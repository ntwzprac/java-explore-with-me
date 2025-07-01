package ru.practicum.mainservice.service;

import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.dto.response.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.dto.response.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestDto> getUserRequests(Long userId);
    ParticipationRequestDto addParticipationRequest(Long userId, Long eventId);
    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
    List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId);
    EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request);
} 