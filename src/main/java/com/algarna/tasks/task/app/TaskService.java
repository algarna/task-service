package com.algarna.tasks.task.app;

import com.algarna.tasks.task.dto.TaskRequest;
import com.algarna.tasks.task.dto.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {
    List<TaskResponse> findAll();
    Page<TaskResponse> findAll(Pageable pageable);
    TaskResponse findById(Long id);
    TaskResponse create(TaskRequest request);
    TaskResponse update(Long id, TaskRequest request);
    void delete(Long id);
}
