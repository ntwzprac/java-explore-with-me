package ru.practicum.mainservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.mapper.CommentMapper;
import ru.practicum.mainservice.dto.request.CommentDto;
import ru.practicum.mainservice.dto.response.CommentViewDto;
import ru.practicum.mainservice.exception.CommentMissingPermissionException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.exception.WrongCommentEventIdException;
import ru.practicum.mainservice.model.Comment;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.CommentRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));
    }

    private void validateIsUserAnAuthor(User user, Long userId) {
        if (!user.getId().equals(userId)) {
            throw new CommentMissingPermissionException("User is not the author of this comment");
        }
    }

    private void validateEventId(Event event, Long eventId) {
        if (!event.getId().equals(eventId)) {
            throw new WrongCommentEventIdException("Event ID does not match the comment's event");
        }
    }

    @Override
    public CommentViewDto addComment(Long userId, Long eventId, CommentDto commentDto) {
        User user = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Cannot add comment to unpublished event");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());

        return CommentMapper.toCommentViewDto(commentRepository.save(comment));
    }

    @Override
    public CommentViewDto editComment(Long userId, Long eventId, Long commentId, CommentDto commentDto) {
        Comment comment = getCommentOrThrow(commentId);
        validateIsUserAnAuthor(comment.getAuthor(), userId);
        validateEventId(comment.getEvent(), eventId);

        comment.setText(commentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());

        return CommentMapper.toCommentViewDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        validateIsUserAnAuthor(comment.getAuthor(), userId);
        validateEventId(comment.getEvent(), eventId);
        commentRepository.delete(comment);
    }

    @Override
    public void deleteCommentAdmin(Long eventId, Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        validateEventId(comment.getEvent(), eventId);
        commentRepository.delete(comment);
    }

    @Override
    public CommentViewDto getComment(Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        return CommentMapper.toCommentViewDto(comment);
    }

    @Override
    public List<CommentViewDto> getComments(Long eventId, int from, int size, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(sortDirection, "updatedOn", "createdOn"));

        List<Comment> comments = commentRepository.findByEventIdOrderByUpdateTimeOrCreateTime(eventId, pageRequest).getContent();
        return comments.stream()
                .map(CommentMapper::toCommentViewDto)
                .toList();
    }
}
