package ru.practicum.mainservice.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventRepositoryTest {
    @Autowired
    private EventRepository eventRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void saveAndFindById() {
        Category category = new Category();
        category.setName("cat1");
        entityManager.persist(category);

        User user = new User();
        user.setName("user1");
        user.setEmail("user1@email.com");
        entityManager.persist(user);

        Event event = new Event();
        event.setTitle("event1");
        event.setAnnotation("annotation");
        event.setDescription("desc");
        event.setEventDate(LocalDateTime.now());
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(null);
        event.setPaid(false);
        event.setParticipantLimit(10);
        event.setRequestModeration(false);
        event.setState(EventState.PENDING);
        event.setConfirmedRequests(0);
        event.setViews(0);

        Event saved = eventRepository.save(event);
        Optional<Event> found = eventRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("event1");
    }
}