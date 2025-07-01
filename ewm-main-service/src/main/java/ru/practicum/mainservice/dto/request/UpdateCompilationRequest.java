package ru.practicum.mainservice.dto.request;

import lombok.*;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50)
    private String title;
    private Boolean pinned;
    private Set<Long> events;
}