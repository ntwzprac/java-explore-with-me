package ru.practicum.mainservice.dto.request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCompilationDto {
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
    private boolean pinned = false;
    private Set<Long> events;
} 