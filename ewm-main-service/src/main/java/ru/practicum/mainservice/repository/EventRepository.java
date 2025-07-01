package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
} 