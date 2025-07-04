package ru.practicum.mainservice.service;

import ru.practicum.mainservice.dto.request.CommentDto;
import ru.practicum.mainservice.dto.response.CommentViewDto;

import java.util.List;

public interface CommentService {
    CommentViewDto addComment(Long userId, Long eventId, CommentDto commentDto);

    CommentViewDto getComment(Long commentId);

    CommentViewDto editComment(Long userId, Long eventId, Long commentId, CommentDto commentDto);

    void deleteComment(Long userId, Long eventId, Long commentId);

    List<CommentViewDto> getComments(Long eventId, int from, int size, String direction);

    void deleteCommentAdmin(Long eventId, Long commentId);
}
