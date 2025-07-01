package ru.practicum.mainservice.dto.request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCategoryDto {
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
} 