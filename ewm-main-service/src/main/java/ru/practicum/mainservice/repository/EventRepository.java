package ru.practicum.mainservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE " +
            "(:users IS NULL OR e.initiator.id IN :users) AND " +
            "(:states IS NULL OR e.state IN :states) AND " +
            "(:categories IS NULL OR e.category.id IN :categories) AND " +
            "(CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart) AND " +
            "(CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd)")
    Page<Event> searchEventsAdmin(@Param("users") List<Long> users,
                                  @Param("states") List<EventState> states,
                                  @Param("categories") List<Long> categories,
                                  @Param("rangeStart") LocalDateTime rangeStart,
                                  @Param("rangeEnd") LocalDateTime rangeEnd,
                                  Pageable pageable);

    Page<Event> findByInitiatorId(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM events e WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL OR LOWER(e.annotation::text) LIKE LOWER(CONCAT('%', CAST(:text AS text), '%')) " +
            "OR LOWER(e.description::text) LIKE LOWER(CONCAT('%', CAST(:text AS text), '%'))) " +
            "AND (:categories IS NULL OR e.category_id IN (:categories)) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (CAST(:rangeStart AS timestamp) IS NULL OR e.event_date >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.event_date <= :rangeEnd)",
            nativeQuery = true)
    Page<Event> searchEventsPublic(@Param("text") String text,
                                   @Param("categories") List<Long> categories,
                                   @Param("paid") Boolean paid,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   Pageable pageable);

    boolean existsByCategory_Id(Long categoryId);
}