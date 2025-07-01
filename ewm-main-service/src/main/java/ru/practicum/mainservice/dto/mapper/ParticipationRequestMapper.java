package ru.practicum.mainservice.dto.mapper;

import ru.practicum.mainservice.dto.response.ParticipationRequestDto;
import ru.practicum.mainservice.model.ParticipationRequest;

import java.time.format.DateTimeFormatter;

public class ParticipationRequestMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto toDto(ParticipationRequest request) {
        if (request == null) return null;
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .created(request.getCreated().format(FORMATTER))
                .build();
    }
} 