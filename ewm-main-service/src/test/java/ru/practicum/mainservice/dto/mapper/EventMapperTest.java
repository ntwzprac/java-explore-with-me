package ru.practicum.mainservice.dto.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.mainservice.dto.request.NewEventDto;
import ru.practicum.mainservice.dto.request.UpdateEventAdminRequest;
import ru.practicum.mainservice.dto.request.UpdateEventUserRequest;
import ru.practicum.mainservice.model.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EventMapperTest {
    @Test
    void toFullDto_nullEvent_returnsNull() {
        assertNull(EventMapper.toFullDto(null));
    }

    @Test
    void toShortDto_nullEvent_returnsNull() {
        assertNull(EventMapper.toShortDto(null));
    }

    @Test
    void toEntity_createsEventFromDto() {
        NewEventDto dto = new NewEventDto();
        dto.setTitle("title");
        dto.setAnnotation("annotation");
        dto.setDescription("desc");
        dto.setLocation(new Location());
        dto.setPaid(true);
        dto.setParticipantLimit(10);
        dto.setRequestModeration(false);
        dto.setEventDate(LocalDateTime.now().plusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        User user = User.builder().id(1L).build();
        Category category = Category.builder().id(1L).build();
        Event event = EventMapper.toEntity(dto, user, category);
        assertEquals(dto.getTitle(), event.getTitle());
        assertEquals(dto.getAnnotation(), event.getAnnotation());
        assertEquals(dto.getDescription(), event.getDescription());
        assertEquals(category, event.getCategory());
        assertEquals(user, event.getInitiator());
        assertEquals(dto.getLocation(), event.getLocation());
        assertEquals(dto.getPaid(), event.getPaid());
        assertEquals(dto.getParticipantLimit(), event.getParticipantLimit());
        assertEquals(dto.getRequestModeration(), event.getRequestModeration());
    }

    @Test
    void updateEntityByAdmin_updatesFields() {
        Event event = Event.builder().state(EventState.PENDING).build();
        UpdateEventAdminRequest dto = new UpdateEventAdminRequest();
        dto.setTitle("newTitle");
        dto.setStateAction("PUBLISH_EVENT");
        Category category = Category.builder().id(2L).build();
        Location location = new Location();
        EventMapper.updateEntityByAdmin(event, dto, category, location);
        assertEquals("newTitle", event.getTitle());
        assertEquals(EventState.PUBLISHED, event.getState());
        assertEquals(category, event.getCategory());
        assertEquals(location, event.getLocation());
    }

    @Test
    void updateEntityByUser_updatesFields() {
        Event event = Event.builder().state(EventState.PENDING).build();
        UpdateEventUserRequest dto = new UpdateEventUserRequest();
        dto.setTitle("userTitle");
        dto.setStateAction("SEND_TO_REVIEW");
        Category category = Category.builder().id(3L).build();
        Location location = new Location();
        EventMapper.updateEntityByUser(event, dto, category, location);
        assertEquals("userTitle", event.getTitle());
        assertEquals(EventState.PENDING, event.getState());
        assertEquals(category, event.getCategory());
        assertEquals(location, event.getLocation());
    }
}