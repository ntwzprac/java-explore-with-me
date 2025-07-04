package ru.practicum.mainservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.service.CommentService;

@Slf4j
@RestController
@RequestMapping("/admin/events/{eventId}/comments")
@RequiredArgsConstructor
@Validated
public class AdminEventCommentController {
    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long eventId,
            @PathVariable Long commentId
    ) {
        log.info("Deleting comment with id: {} for event with id: {}", commentId, eventId);
        commentService.deleteCommentAdmin(eventId, commentId);
        log.info("Comment with id: {} for event with id: {} successfully deleted", commentId, eventId);
    }
}
