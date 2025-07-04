package ru.practicum.mainservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.CommentDto;
import ru.practicum.mainservice.dto.response.CommentViewDto;
import ru.practicum.mainservice.service.CommentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@Validated
public class UserEventCommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentViewDto addComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody CommentDto commentDto
    ) {
        log.info("Adding new comment for user id: {}, event id: {}", userId, eventId);
        CommentViewDto commentViewDto = commentService.addComment(userId, eventId, commentDto);
        log.info("Comment added successfully for user id: {}, event id: {}", userId, eventId);
        return commentViewDto;
    }

    @PatchMapping("/{commentId}")
    public CommentViewDto updateComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @RequestBody CommentDto commentDto,
            @PathVariable Long eventId) {
        log.info("Updating comment id: {} for user id: {}, event id: {}", commentId, userId, eventId);
        CommentViewDto updatedComment = commentService.editComment(userId, eventId, commentId, commentDto);
        log.info("Comment updated successfully for user id: {}, event id: {}", userId, eventId);
        return updatedComment;
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @PathVariable Long eventId) {
        log.info("Deleting comment id: {} for user id: {}, event id: {}", commentId, userId, eventId);
        commentService.deleteComment(userId, eventId, commentId);
        log.info("Comment deleted successfully for user id: {}, event id: {}", userId, eventId);
    }
}
