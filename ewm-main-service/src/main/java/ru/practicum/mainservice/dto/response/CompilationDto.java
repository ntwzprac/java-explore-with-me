package ru.practicum.mainservice.dto.response;

import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {
    private Long id;
    private String title;
    private boolean pinned;
    private Set<Long> events;
} 