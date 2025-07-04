package ru.practicum.mainservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.mainservice.dto.request.CommentDto;
import ru.practicum.mainservice.dto.response.CommentViewDto;
import ru.practicum.mainservice.exception.CommentMissingPermissionException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.Comment;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.CommentRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.impl.CommentServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Event event;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.com");

        event = new Event();
        event.setId(1L);
        event.setState(EventState.PUBLISHED);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setText("Test comment");
    }

    @Test
    void addComment_ShouldCreateNewComment() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentViewDto result = commentService.addComment(user.getId(), event.getId(), commentDto);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(user.getName(), result.getAuthor().getName());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_ShouldThrowNotFoundException_WhenEventNotPublished() {
        event.setState(EventState.PENDING);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        assertThrows(NotFoundException.class,
                () -> commentService.addComment(user.getId(), event.getId(), commentDto));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> commentService.addComment(user.getId(), event.getId(), commentDto));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void editComment_ShouldUpdateComment() {
        CommentDto updateDto = new CommentDto();
        updateDto.setText("Updated comment");

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentViewDto result = commentService.editComment(user.getId(), event.getId(), comment.getId(), updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getText(), result.getText());
        assertNotNull(result.getUpdatedOn());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void editComment_ShouldThrowCommentMissingPermissionException_WhenNotAuthor() {
        User otherUser = new User();
        otherUser.setId(2L);

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        assertThrows(CommentMissingPermissionException.class,
                () -> commentService.editComment(otherUser.getId(), event.getId(), comment.getId(), commentDto));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getComments_ShouldReturnCommentsList() {
        int from = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(from / size, size);

        when(commentRepository.findByEventIdOrderByUpdateTimeOrCreateTime(eq(event.getId()), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(comment)));

        List<CommentViewDto> results = commentService.getComments(event.getId(), from, size, "ASC");

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(comment.getId(), results.get(0).getId());
        assertEquals(comment.getText(), results.get(0).getText());
    }

    @Test
    void getComment_ShouldReturnComment() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        CommentViewDto result = commentService.getComment(comment.getId());

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
    }

    @Test
    void deleteComment_ShouldDeleteComment() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        commentService.deleteComment(user.getId(), event.getId(), comment.getId());

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteCommentAdmin_ShouldDeleteComment() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        commentService.deleteCommentAdmin(event.getId(), comment.getId());

        verify(commentRepository).delete(comment);
    }
}