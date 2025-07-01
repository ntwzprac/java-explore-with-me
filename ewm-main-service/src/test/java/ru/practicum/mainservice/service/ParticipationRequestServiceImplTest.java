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
    }

    @Test
    void getRequestById_ShouldReturnRequest() {
        when(participationRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        assertDoesNotThrow(() -> participationRequestService.getUserRequests(1L));
        verify(participationRequestRepository, times(1)).findById(1L);
    }

    @Test
    void getRequestById_NotFound_ShouldThrowException() {
        when(participationRequestRepository.findById(2L)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> participationRequestService.getUserRequests(2L));
        verify(participationRequestRepository, times(1)).findById(2L);
    }
}