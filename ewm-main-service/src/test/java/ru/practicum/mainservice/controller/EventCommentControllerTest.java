package ru.practicum.mainservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.mainservice.dto.response.CommentViewDto;
import ru.practicum.mainservice.dto.response.UserShortDto;
import ru.practicum.mainservice.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventCommentController.class)
class EventCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    @Test
    void getComments_ShouldReturnCommentsList() throws Exception {
        Long eventId = 1L;
        UserShortDto author = UserShortDto.builder()
                .id(1L)
                .name("Test Author")
                .build();

        CommentViewDto comment = CommentViewDto.builder()
                .id(1L)
                .text("Test comment")
                .author(author)
                .createdOn(LocalDateTime.now().toString())
                .build();

        List<CommentViewDto> comments = List.of(comment);

        when(commentService.getComments(eq(eventId), anyInt(), anyInt(), eq("ASC")))
                .thenReturn(comments);

        mockMvc.perform(get("/events/{eventId}/comments", eventId)
                        .param("from", "0")
                        .param("size", "10")
                        .param("sortOrder", "ASC"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(comments)));
    }

    @Test
    void getComment_ShouldReturnComment() throws Exception {
        Long eventId = 1L;
        Long commentId = 1L;
        UserShortDto author = UserShortDto.builder()
                .id(1L)
                .name("Test Author")
                .build();

        CommentViewDto comment = CommentViewDto.builder()
                .id(commentId)
                .text("Test comment")
                .author(author)
                .createdOn(LocalDateTime.now().toString())
                .build();

        when(commentService.getComment(commentId)).thenReturn(comment);

        mockMvc.perform(get("/events/{eventId}/comments/{commentId}", eventId, commentId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(comment)));
    }
} 