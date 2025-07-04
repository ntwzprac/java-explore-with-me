package ru.practicum.mainservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.mainservice.dto.request.CommentDto;
import ru.practicum.mainservice.dto.response.CommentViewDto;
import ru.practicum.mainservice.dto.response.UserShortDto;
import ru.practicum.mainservice.service.CommentService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserEventCommentController.class)
class UserEventCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    @Test
    void addComment_ShouldCreateCommentAndReturn201() throws Exception {
        Long userId = 1L;
        Long eventId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");

        UserShortDto author = UserShortDto.builder()
                .id(userId)
                .name("Test Author")
                .build();

        CommentViewDto expectedResponse = CommentViewDto.builder()
                .id(1L)
                .text(commentDto.getText())
                .author(author)
                .createdOn(LocalDateTime.now().toString())
                .build();

        when(commentService.addComment(eq(userId), eq(eventId), any(CommentDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/users/{userId}/events/{eventId}/comments", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResponse)));
    }

    @Test
    void updateComment_ShouldUpdateCommentAndReturn200() throws Exception {
        Long userId = 1L;
        Long eventId = 1L;
        Long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Updated comment");

        UserShortDto author = UserShortDto.builder()
                .id(userId)
                .name("Test Author")
                .build();

        CommentViewDto expectedResponse = CommentViewDto.builder()
                .id(commentId)
                .text(commentDto.getText())
                .author(author)
                .createdOn(LocalDateTime.now().toString())
                .updatedOn(LocalDateTime.now().toString())
                .build();

        when(commentService.editComment(eq(userId), eq(eventId), eq(commentId), any(CommentDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(patch("/users/{userId}/events/{eventId}/comments/{commentId}", userId, eventId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResponse)));
    }

    @Test
    void deleteComment_ShouldDeleteCommentAndReturn204() throws Exception {
        Long userId = 1L;
        Long eventId = 1L;
        Long commentId = 1L;

        doNothing().when(commentService).deleteComment(userId, eventId, commentId);

        mockMvc.perform(delete("/users/{userId}/events/{eventId}/comments/{commentId}", userId, eventId, commentId))
                .andExpect(status().isNoContent());
    }
}