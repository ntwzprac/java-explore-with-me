package ru.practicum.mainservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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