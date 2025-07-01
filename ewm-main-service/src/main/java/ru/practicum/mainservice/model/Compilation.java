package ru.practicum.mainservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "compilations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private boolean pinned = false;

    // Пока связь с событиями через Set<Long> eventIds, позже заменю на Set<Event>
    @ElementCollection
    @CollectionTable(name = "compilation_events", joinColumns = @JoinColumn(name = "compilation_id"))
    @Column(name = "event_id")
    private Set<Long> events;
} 