package ru.practicum.mainservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.mainservice.service.CommentService;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminEventCommentController.class)
class AdminEventCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    @Test
    void deleteComment_ShouldDeleteCommentAndReturn204() throws Exception {
        Long eventId = 1L;
        Long commentId = 1L;

        doNothing().when(commentService).deleteCommentAdmin(eventId, commentId);

        mockMvc.perform(delete("/admin/events/{eventId}/comments/{commentId}", eventId, commentId))
                .andExpect(status().isNoContent());

        verify(commentService).deleteCommentAdmin(eventId, commentId);
    }
}