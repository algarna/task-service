package com.algarna.tasks.task.dto;

import com.algarna.tasks.task.domain.Task;
import java.time.Instant;

public record TaskResponse(
        Long id,
        String title,
        String description,
        boolean done,
        Instant createdAt,
        Instant updatedAt
) {
    public static TaskResponse fromEntity(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isDone(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
