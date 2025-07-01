package ru.practicum.mainservice.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequestDto {
    private Long id;
    private Long event;
    private Long requester;
    private String status;
    private String created;
} 