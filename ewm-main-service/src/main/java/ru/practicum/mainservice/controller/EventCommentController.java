package ru.practicum.mainservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.response.CommentViewDto;
import ru.practicum.mainservice.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
@Validated
public class EventCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentViewDto> getComments(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String sortOrder
    ) {
        log.info("Getting comments for event with id: {} from: {} size: {}", eventId, from, size);
        List<CommentViewDto> comments = commentService.getComments(eventId, from, size, sortOrder.toUpperCase());
        log.info("Found {} comments for event with id: {}", comments.size(), eventId);
        return comments;
    }

    @GetMapping("/{commentId}")
    public CommentViewDto getComment(
            @PathVariable Long eventId,
            @PathVariable Long commentId
    ) {
        log.info("Getting comment with id: {} for event with id: {}", commentId, eventId);
        CommentViewDto comment = commentService.getComment(commentId);
        log.info("Comment found: {}", comment);
        return comment;
    }
}
