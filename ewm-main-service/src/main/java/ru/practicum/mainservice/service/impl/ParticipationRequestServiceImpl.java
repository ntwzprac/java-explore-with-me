package ru.practicum.mainservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.mapper.ParticipationRequestMapper;
import ru.practicum.mainservice.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.dto.response.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.dto.response.ParticipationRequestDto;
import ru.practicum.mainservice.exception.ConflictException;
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
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String STATUS_CANCELED = "CANCELED";

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestRepository.findByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        User user = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);
        validateParticipationRequest(user, event);

        String status = getInitialRequestStatus(event);
        if (STATUS_CONFIRMED.equals(status)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
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
        ParticipationRequest request = getRequestOrThrow(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Request does not belong to user: " + userId);
        }
        request.setStatus(STATUS_CANCELED);
        return ParticipationRequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        return requestRepository.findByEventId(eventId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest req) {
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event does not belong to user: " + userId);
        }
        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(req.getRequestIds());
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();
        List<ParticipationRequest> toSave = new ArrayList<>();
        
        int limit = event.getParticipantLimit();
        int confirmedCount = event.getConfirmedRequests();
        
        for (ParticipationRequest r : requests) {
            if (!STATUS_PENDING.equals(r.getStatus())) {
                throw new ConflictException("Request must have status PENDING");
            }
            if (STATUS_CONFIRMED.equals(req.getStatus())) {
                if (limit == 0 || confirmedCount < limit) {
                    r.setStatus(STATUS_CONFIRMED);
                    confirmed.add(ParticipationRequestMapper.toDto(r));
                    confirmedCount++;
                } else {
                    r.setStatus(STATUS_REJECTED);
                    rejected.add(ParticipationRequestMapper.toDto(r));
                }
            } else if (STATUS_REJECTED.equals(req.getStatus())) {
                r.setStatus(STATUS_REJECTED);
                rejected.add(ParticipationRequestMapper.toDto(r));
            }
            toSave.add(r);
        }

        if (limit != 0 && confirmedCount >= limit) {
            List<ParticipationRequest> pending = requestRepository.findByEventIdAndStatus(eventId, STATUS_PENDING);
            for (ParticipationRequest r : pending) {
                r.setStatus(STATUS_REJECTED);
                rejected.add(ParticipationRequestMapper.toDto(r));
                toSave.add(r);
            }
        }

        requestRepository.saveAll(toSave);
        event.setConfirmedRequests(confirmedCount);
        eventRepository.save(event);
        
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(rejected)
                .build();
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
    }

    private ParticipationRequest getRequestOrThrow(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found: " + requestId));
    }

    private void validateParticipationRequest(User user, Event event) {
        if (event.getInitiator().getId().equals(user.getId())) {
            throw new ConflictException("Initiator cannot request participation in their own event");
        }
        if (!"PUBLISHED".equals(event.getState().name())) {
            throw new ConflictException("Cannot participate in unpublished event");
        }
        if (requestRepository.existsByRequesterIdAndEventId(user.getId(), event.getId())) {
            throw new ConflictException("Duplicate participation request");
        }
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit reached");
        }
    }

    private String getInitialRequestStatus(Event event) {
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return STATUS_CONFIRMED;
        } else {
            return STATUS_PENDING;
        }
    }
}