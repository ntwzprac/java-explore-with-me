package ru.practicum.mainservice.dto.request;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private String status;
} 