package ru.practicum.mainservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentViewDto {
    public Long id;
    public String text;
    public UserShortDto author;
    public String createdOn;
    public String updatedOn;
}