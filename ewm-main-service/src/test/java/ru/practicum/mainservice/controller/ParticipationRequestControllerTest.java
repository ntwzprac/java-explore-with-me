package ru.practicum.mainservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.mainservice.dto.response.ParticipationRequestDto;
import ru.practicum.mainservice.service.ParticipationRequestService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ParticipationRequestControllerTest {
    @Mock
    private ParticipationRequestService requestService;

    @InjectMocks
    private ParticipationRequestController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserRequests() {
        Long userId = 1L;
        ParticipationRequestDto dto = mock(ParticipationRequestDto.class);
        when(requestService.getUserRequests(userId)).thenReturn(Collections.singletonList(dto));

        List<ParticipationRequestDto> result = controller.getUserRequests(userId);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(requestService).getUserRequests(userId);
    }

    @Test
    void addParticipationRequest() {
        Long userId = 1L;
        Long eventId = 2L;
        ParticipationRequestDto dto = mock(ParticipationRequestDto.class);
        when(requestService.addParticipationRequest(userId, eventId)).thenReturn(dto);

        ParticipationRequestDto result = controller.addParticipationRequest(userId, eventId);
        assertNotNull(result);
        verify(requestService).addParticipationRequest(userId, eventId);
    }

    @Test
    void cancelRequest() {
        Long userId = 1L;
        Long requestId = 3L;
        ParticipationRequestDto dto = mock(ParticipationRequestDto.class);
        when(requestService.cancelRequest(userId, requestId)).thenReturn(dto);

        ParticipationRequestDto result = controller.cancelRequest(userId, requestId);
        assertNotNull(result);
        verify(requestService).cancelRequest(userId, requestId);
    }
}