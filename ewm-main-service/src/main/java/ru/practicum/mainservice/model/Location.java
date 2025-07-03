package ru.practicum.mainservice.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    private Float lat;
    private Float lon;
}