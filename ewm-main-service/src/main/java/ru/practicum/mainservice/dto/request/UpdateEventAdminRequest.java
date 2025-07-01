package ru.practicum.mainservice.dto.request;

import lombok.*;
import ru.practicum.mainservice.model.Location;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventAdminRequest {
    private String title;
    private String annotation;
    private String description;
    private Long category;
    private Location location;
    private String eventDate;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
} 