package com.algarna.tasks.task.app;

import com.algarna.tasks.task.domain.Task;
import com.algarna.tasks.task.dto.TaskRequest;
import com.algarna.tasks.task.dto.TaskResponse;
import com.algarna.tasks.task.infra.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    public TaskServiceImpl(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TaskResponse> findAll() {
        return repository.findAll().stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Override
    public Page<TaskResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(TaskResponse::fromEntity);
    }

    @Override
    public TaskResponse findById(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        return TaskResponse.fromEntity(task);
    }

    @Override
    public TaskResponse create(TaskRequest request) {
        Task task = new Task(request.title(), request.description());
        task.setDone(request.done());
        Task saved = repository.save(task);
        return TaskResponse.fromEntity(saved);
    }

    @Override
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDone(request.done());
        Task updated = repository.save(task);
        return TaskResponse.fromEntity(updated);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
