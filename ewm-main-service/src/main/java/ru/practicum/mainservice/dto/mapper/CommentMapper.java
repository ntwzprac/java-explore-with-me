package ru.practicum.mainservice.dto.mapper;

import ru.practicum.mainservice.dto.response.CommentViewDto;
import ru.practicum.mainservice.model.Comment;

import java.time.format.DateTimeFormatter;

public class CommentMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static CommentViewDto toCommentViewDto(Comment comment) {
        return CommentViewDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn().format(FORMATTER))
                .updatedOn(comment.getUpdatedOn() != null ? comment.getUpdatedOn().format(FORMATTER) : null)
                .author(UserMapper.toShortDto(comment.getAuthor()))
                .build();
    }
}
