package com.algarna.tasks.task.infra;

import com.algarna.tasks.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /*---- Query methods ----*/

    List<Task> findByDone(boolean done);

    List<Task> findByTitleContainingIgnoreCase(String keyword);

}
