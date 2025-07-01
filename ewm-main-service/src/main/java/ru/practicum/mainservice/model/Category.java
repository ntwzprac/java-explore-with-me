package ru.practicum.mainservice.model;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;
} 