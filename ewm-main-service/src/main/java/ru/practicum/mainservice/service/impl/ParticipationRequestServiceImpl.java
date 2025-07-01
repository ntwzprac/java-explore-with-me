package ru.practicum.mainservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.mapper.ParticipationRequestMapper;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.dto.response.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.dto.response.ParticipationRequestDto;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.ParticipationRequest;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.ParticipationRequestRepository;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.ParticipationRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestRepository.findByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ru.practicum.mainservice.exception.ConflictException("Initiator cannot request participation in their own event");
        }
        if (!event.getState().name().equals("PUBLISHED")) {
            throw new ru.practicum.mainservice.exception.ConflictException("Cannot participate in unpublished event");
        }
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ru.practicum.mainservice.exception.ConflictException("Duplicate participation request");
        }
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ru.practicum.mainservice.exception.ConflictException("Participant limit reached");
        }
        String status;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            status = "CONFIRMED";
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            status = "PENDING";
        }
        ParticipationRequest request = ParticipationRequest.builder()
                .event(event)
                .requester(user)
                .status(status)
                .created(LocalDateTime.now())
                .build();
        ParticipationRequest saved = requestRepository.save(request);
        return ParticipationRequestMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found: " + requestId));
        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Request does not belong to user: " + userId);
        }
        request.setStatus("CANCELED");
        return ParticipationRequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        return requestRepository.findByEventId(eventId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest req) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event does not belong to user: " + userId);
        }
        List<ParticipationRequest> requests = requestRepository.findAllById(req.getRequestIds());
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();
        int limit = event.getParticipantLimit();
        int confirmedCount = event.getConfirmedRequests();
        for (ParticipationRequest r : requests) {
            if (!r.getStatus().equals("PENDING")) {
                throw new ru.practicum.mainservice.exception.ConflictException("Request must have status PENDING");
            }
            if ("CONFIRMED".equals(req.getStatus())) {
                if (limit == 0 || confirmedCount < limit) {
                    r.setStatus("CONFIRMED");
                    confirmed.add(ParticipationRequestMapper.toDto(requestRepository.save(r)));
                    confirmedCount++;
                } else {
                    r.setStatus("REJECTED");
                    rejected.add(ParticipationRequestMapper.toDto(requestRepository.save(r)));
                }
            } else if ("REJECTED".equals(req.getStatus())) {
                r.setStatus("REJECTED");
                rejected.add(ParticipationRequestMapper.toDto(requestRepository.save(r)));
            }
        }
        if (limit != 0 && confirmedCount >= limit) {
            List<ParticipationRequest> pending = requestRepository.findByEventId(eventId).stream()
                    .filter(r -> r.getStatus().equals("PENDING"))
                    .collect(java.util.stream.Collectors.toList());
            for (ParticipationRequest r : pending) {
                r.setStatus("REJECTED");
                rejected.add(ParticipationRequestMapper.toDto(requestRepository.save(r)));
            }
        }
        event.setConfirmedRequests(confirmedCount);
        eventRepository.save(event);
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(rejected)
                .build();
    }
}