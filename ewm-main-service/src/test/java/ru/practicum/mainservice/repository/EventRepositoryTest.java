package ru.practicum.mainservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.mainservice.model.Event;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventRepositoryTest {
    @Autowired
    private EventRepository eventRepository;

    @Test
    void saveAndFindById() {
        Event event = new Event();
        event.setTitle("event1");
        Event saved = eventRepository.save(event);
        Optional<Event> found = eventRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("event1");
    }
}