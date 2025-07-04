package ru.practicum.mainservice.dto.mapper;

import ru.practicum.mainservice.dto.request.NewEventDto;
import ru.practicum.mainservice.dto.request.UpdateEventAdminRequest;
import ru.practicum.mainservice.dto.request.UpdateEventUserRequest;
import ru.practicum.mainservice.dto.response.EventFullDto;
import ru.practicum.mainservice.dto.response.EventShortDto;
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
                .commentsAmount(event.getComments() != null ? event.getComments().size() : 0)
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
                .commentsAmount(event.getComments() != null ? event.getComments().size() : 0)
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

    private static void updateCommonFields(Event event, String title, String annotation, String description, Category category, Location location, String eventDate, Boolean paid, Integer participantLimit, Boolean requestModeration) {
        if (title != null) event.setTitle(title);
        if (annotation != null) event.setAnnotation(annotation);
        if (description != null) event.setDescription(description);
        if (category != null) event.setCategory(category);
        if (location != null) event.setLocation(location);
        if (eventDate != null)
            event.setEventDate(java.time.LocalDateTime.parse(eventDate, FORMATTER));
        if (paid != null) event.setPaid(paid);
        if (participantLimit != null) event.setParticipantLimit(participantLimit);
        if (requestModeration != null) event.setRequestModeration(requestModeration);
    }

    public static void updateEntityByAdmin(Event event, UpdateEventAdminRequest dto, Category category, Location location) {
        updateCommonFields(event, dto.getTitle(), dto.getAnnotation(), dto.getDescription(), category, location, dto.getEventDate(), dto.getPaid(), dto.getParticipantLimit(), dto.getRequestModeration());
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "PUBLISH_EVENT" -> {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(java.time.LocalDateTime.now());
                }
                case "REJECT_EVENT" -> event.setState(EventState.CANCELED);
            }
        }
    }

    public static void updateEntityByUser(Event event, UpdateEventUserRequest dto, Category category, Location location) {
        updateCommonFields(event, dto.getTitle(), dto.getAnnotation(), dto.getDescription(), category, location, dto.getEventDate(), dto.getPaid(), dto.getParticipantLimit(), dto.getRequestModeration());
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "SEND_TO_REVIEW" -> event.setState(EventState.PENDING);
                case "CANCEL_REVIEW" -> event.setState(EventState.CANCELED);
            }
        }
    }
}