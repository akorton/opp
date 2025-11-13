package ru.tgu.opp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tgu.opp.dto.CreateTaskDto;
import ru.tgu.opp.model.Task;
import ru.tgu.opp.model.TaskStatus;
import ru.tgu.opp.repository.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Task create(CreateTaskDto dto) {
        var task = Task.builder()
                .description(dto.getDescription())
                .deadline(dto.getDeadline())
                .status(TaskStatus.NOT_FINISHED)
                .executors(List.of())
                .prerequisites(List.of())
                .build();
        return taskRepository.save(task);
    }
}
