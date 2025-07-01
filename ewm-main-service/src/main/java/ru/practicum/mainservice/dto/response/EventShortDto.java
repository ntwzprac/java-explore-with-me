package ru.practicum.mainservice.dto.response;

import lombok.*;
import ru.practicum.mainservice.model.Location;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private UserShortDto initiator;
    private Boolean paid;
    private String eventDate;
    private Integer confirmedRequests;
    private Integer views;
} 