package ru.practicum.mainservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Size(max = 2000, message = "Annotation cannot exceed 2000 symbols")
    @NotBlank
    private String annotation;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Size(max = 7000, message = "Description cannot exceed 7000 symbols")
    @NotBlank
    private String description;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    private LocalDateTime publishedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Embedded
    private Location location;

    @Column(nullable = false)
    private Boolean paid;

    @Column(nullable = false)
    private Integer participantLimit;

    @Column(nullable = false)
    private Boolean requestModeration;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(nullable = false)
    private Integer confirmedRequests;

    @Column(nullable = false)
    private Integer views;
}