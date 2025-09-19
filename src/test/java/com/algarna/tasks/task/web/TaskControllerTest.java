package com.algarna.tasks.task.web;

import com.algarna.tasks.task.app.TaskService;
import com.algarna.tasks.task.dto.TaskRequest;
import com.algarna.tasks.task.dto.TaskResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TaskService service;

    @Test
    void getAllTasks_returnsList() throws Exception {
        var t1 = new TaskResponse(1L, "A", "a", false, Instant.now(), Instant.now());
        var t2 = new TaskResponse(2L, "B", "b", true, Instant.now(), Instant.now());
        given(service.findAll()).willReturn(List.of(t1, t2));

        mvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("A"));
    }

    @Test
    void createTask_returns201_andBody() throws Exception {
        var created = new TaskResponse(1L, "Buy milk", "Semi", false, Instant.now(), Instant.now());
        given(service.create(any(TaskRequest.class))).willReturn(created);

        mvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "title": "Buy milk",
                          "description": "Semi",
                          "done": false
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/tasks/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Buy milk"));
    }

    @Test
    void createTask_withInvalidTitle_returns400() throws Exception {
        mvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "title": "",
                          "description": "bad",
                          "done": false
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations[0].field").value("title"));
    }

    @Test
    void getById_returns200_withBody() throws Exception {
        var t = new TaskResponse(7L, "Read", "docs", false, Instant.now(), Instant.now());
        given(service.findById(7L)).willReturn(t);

        mvc.perform(get("/api/v1/tasks/{id}", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.title").value("Read"));
    }

    @Test
    void getById_whenMissing_returns404_withErrorBody() throws Exception {
        // given
        long id = 42L;
        given(service.findById(id)).willThrow(new IllegalArgumentException("Task not found: " + id));

        // when/then
        mvc.perform(get("/api/v1/tasks/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.path").value("/api/v1/tasks/42"))
                .andExpect(jsonPath("$.message").value(containsString("Task not found")));
    }
}
