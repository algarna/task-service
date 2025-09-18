package com.algarna.tasks.task.app;

import com.algarna.tasks.task.domain.Task;
import com.algarna.tasks.task.dto.TaskRequest;
import com.algarna.tasks.task.dto.TaskResponse;
import com.algarna.tasks.task.infra.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    TaskRepository repository;

    TaskServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TaskServiceImpl(repository);
    }

    @Test
    void findAll_returnsDtoList() {
        Task t1 = new Task("A", "a"); t1.setDone(false);
        Task t2 = new Task("B", "b"); t2.setDone(true);
        when(repository.findAll()).thenReturn(List.of(t1, t2));

        List<TaskResponse> out = service.findAll();

        assertEquals(2, out.size());
        assertEquals("A", out.get(0).title());
        assertTrue(out.get(1).done());
        verify(repository).findAll();
    }

    @Test
    void findById_whenExists_returnsDto() {
        Task t = new Task("Title", "Desc"); t.setDone(false);
        when(repository.findById(10L)).thenReturn(Optional.of(t));

        TaskResponse out = service.findById(10L);

        assertEquals("Title", out.title());
        assertFalse(out.done());
        verify(repository).findById(10L);
    }

    @Test
    void findById_whenMissing_throwsIllegalArgument() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.findById(999L));
        verify(repository).findById(999L);
    }

    @Test
    void create_mapsRequest_saves_andReturnsDto() {
        TaskRequest req = new TaskRequest("Buy milk", "Semi-skimmed", false);

        // Capture what we save to verify mapping
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse out = service.create(req);

        verify(repository).save(captor.capture());
        Task saved = captor.getValue();
        assertEquals("Buy milk", saved.getTitle());
        assertEquals("Semi-skimmed", saved.getDescription());
        assertFalse(saved.isDone());

        assertEquals("Buy milk", out.title());
        assertEquals("Semi-skimmed", out.description());
    }

    @Test
    void update_whenExists_appliesChanges_andReturnsDto() {
        Task existing = new Task("Old", "Old desc"); existing.setDone(false);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskRequest req = new TaskRequest("New", "New desc", true);
        TaskResponse out = service.update(1L, req);

        assertEquals("New", out.title());
        assertTrue(out.done());
        verify(repository).save(existing);
    }

    @Test
    void update_whenMissing_throwsIllegalArgument() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        TaskRequest req = new TaskRequest("X","Y", false);
        assertThrows(IllegalArgumentException.class, () -> service.update(2L, req));
        verify(repository, never()).save(any());
    }

    @Test
    void delete_callsRepository() {
        service.delete(7L);
        verify(repository).deleteById(7L);
    }

}
