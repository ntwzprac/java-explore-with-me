package ru.practicum.mainservice.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long id;
    private String name;
} 