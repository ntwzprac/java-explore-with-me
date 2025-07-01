package ru.practicum.mainservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.mainservice.model.ParticipationRequest;
import ru.practicum.mainservice.repository.ParticipationRequestRepository;
import ru.practicum.mainservice.service.impl.ParticipationRequestServiceImpl;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.model.EventState;
import java.time.LocalDateTime;
import ru.practicum.mainservice.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipationRequestServiceImplTest {
    @Mock
    private ParticipationRequestRepository participationRequestRepository;
    @InjectMocks
    private ParticipationRequestServiceImpl participationRequestService;
    private ParticipationRequest request;

    @BeforeEach
    void setUp() {
        request = new ParticipationRequest();
        request.setId(1L);
        request.setCreated(LocalDateTime.now());
    }

    @Test
    void getRequestById_ShouldReturnRequest() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("event1");
        event.setAnnotation("annotation");
        event.setDescription("desc");
        event.setEventDate(LocalDateTime.now());
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(new User(1L, "user1", "user1@email.com"));
        event.setCategory(new Category(1L, "cat1"));
        event.setLocation(null);
        event.setPaid(false);
        event.setParticipantLimit(10);
        event.setRequestModeration(false);
        event.setState(EventState.PENDING);
        event.setConfirmedRequests(0);
        event.setViews(0);
        request.setEvent(event);
        request.setRequester(new User(1L, "user1", "user1@email.com"));
        when(participationRequestRepository.findByRequesterId(1L)).thenReturn(java.util.Collections.singletonList(request));
        assertDoesNotThrow(() -> participationRequestService.getUserRequests(1L));
        verify(participationRequestRepository, times(1)).findByRequesterId(1L);
    }

    @Test
    void getRequestById_NotFound_ShouldThrowException() {
        when(participationRequestRepository.findByRequesterId(2L)).thenReturn(java.util.Collections.emptyList());
        assertDoesNotThrow(() -> participationRequestService.getUserRequests(2L));
        verify(participationRequestRepository, times(1)).findByRequesterId(2L);
    }
}