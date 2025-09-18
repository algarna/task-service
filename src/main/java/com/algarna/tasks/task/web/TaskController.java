package com.algarna.tasks.task.web;

import com.algarna.tasks.task.app.TaskService;
import com.algarna.tasks.task.dto.TaskRequest;
import com.algarna.tasks.task.dto.TaskResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<TaskResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/page")
    public Page<TaskResponse> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request,
                                               UriComponentsBuilder uriBuilder) {
        TaskResponse created = service.create(request);
        URI location = uriBuilder
                .path("/api/v1/tasks/{id}")
                .build(created.id());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
