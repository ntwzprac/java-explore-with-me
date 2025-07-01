package ru.practicum.mainservice.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserShortDto {
    private Long id;
    private String name;
} 