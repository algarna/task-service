package com.algarna.tasks.task.web;

import com.algarna.tasks.task.dto.TaskRequest;
import com.algarna.tasks.task.dto.TaskResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskApiIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void createAndGetTask() {
        // --- create ---
        TaskRequest req = new TaskRequest("Integration test", "via TestRestTemplate", false);
        ResponseEntity<TaskResponse> createResp = rest.postForEntity(url("/api/v1/tasks"), req, TaskResponse.class);

        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        TaskResponse created = createResp.getBody();
        Assertions.assertNotNull(created);
        assertThat(created.title()).isEqualTo("Integration test");

        // --- get ---
        ResponseEntity<TaskResponse> getResp = rest.getForEntity(url("/api/v1/tasks/" + created.id()), TaskResponse.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(getResp.getBody());
        assertThat(getResp.getBody().title()).isEqualTo("Integration test");
    }

    @Test
    void validationError_returnsBadRequest() throws Exception {
        TaskRequest invalid = new TaskRequest("", "desc", false);

        ResponseEntity<String> resp =
                rest.postForEntity(url("/api/v1/tasks"), invalid, String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        JsonNode root = new ObjectMapper().readTree(resp.getBody());
        JsonNode v0 = root.at("/violations/0");
        assertThat(v0.get("field").asText()).isEqualTo("title");
        assertThat(v0.get("message").asText()).isNotBlank();
    }
}
