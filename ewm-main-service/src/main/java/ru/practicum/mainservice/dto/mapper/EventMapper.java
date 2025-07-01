package ru.practicum.mainservice.dto.mapper;

import ru.practicum.mainservice.dto.request.NewEventDto;
import ru.practicum.mainservice.dto.request.UpdateEventAdminRequest;
import ru.practicum.mainservice.dto.request.UpdateEventUserRequest;
import ru.practicum.mainservice.dto.response.*;
import ru.practicum.mainservice.model.*;

import java.time.format.DateTimeFormatter;

public class EventMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EventFullDto toFullDto(Event event) {
        if (event == null) return null;
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(CategoryMapper.toDto(event.getCategory()))
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .eventDate(event.getEventDate().format(FORMATTER))
                .createdOn(event.getCreatedOn().format(FORMATTER))
                .publishedOn(event.getPublishedOn() != null ? event.getPublishedOn().format(FORMATTER) : null)
                .state(event.getState().name())
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toShortDto(Event event) {
        if (event == null) return null;
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .eventDate(event.getEventDate().format(FORMATTER))
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .build();
    }

    public static Event toEntity(NewEventDto dto, User initiator, Category category) {
        return Event.builder()
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .category(category)
                .initiator(initiator)
                .location(dto.getLocation())
                .paid(dto.getPaid() != null ? dto.getPaid() : false)
                .participantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0)
                .requestModeration(dto.getRequestModeration() != null ? dto.getRequestModeration() : true)
                .eventDate(java.time.LocalDateTime.parse(dto.getEventDate(), FORMATTER))
                .createdOn(java.time.LocalDateTime.now())
                .state(EventState.PENDING)
                .confirmedRequests(0)
                .views(0)
                .build();
    }

    public static void updateEntityByAdmin(Event event, UpdateEventAdminRequest dto, Category category, Location location) {
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (category != null) event.setCategory(category);
        if (location != null) event.setLocation(location);
        if (dto.getEventDate() != null) event.setEventDate(java.time.LocalDateTime.parse(dto.getEventDate(), FORMATTER));
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "PUBLISH_EVENT" -> event.setState(EventState.PUBLISHED);
                case "REJECT_EVENT" -> event.setState(EventState.CANCELED);
            }
        }
    }

    public static void updateEntityByUser(Event event, UpdateEventUserRequest dto, Category category, Location location) {
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (category != null) event.setCategory(category);
        if (location != null) event.setLocation(location);
        if (dto.getEventDate() != null) event.setEventDate(java.time.LocalDateTime.parse(dto.getEventDate(), FORMATTER));
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "SEND_TO_REVIEW" -> event.setState(EventState.PENDING);
                case "CANCEL_REVIEW" -> event.setState(EventState.CANCELED);
            }
        }
    }
} 