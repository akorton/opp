package ru.tgu.opp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tgu.opp.dto.CreateTaskDto;
import ru.tgu.opp.dto.TaskDto;
import ru.tgu.opp.dto.TaskStatusDto;
import ru.tgu.opp.model.*;
import ru.tgu.opp.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;

    public Task create(CreateTaskDto dto, Project project, boolean finalTask) {
        var currentUser = userService.getCurrentUser();

        var task = Task.builder()
                .project(project)
                .description(dto.getDescription())
                .deadline(Objects.isNull(dto.getDeadline()) ? LocalDateTime.now() : dto.getDeadline())
                .status(TaskStatus.NOT_FINISHED)
                .executors(Objects.isNull(dto.getExecutorIds())
                        ? List.of()
                        : dto.getExecutorIds().stream()
                        .map(userService::getExecutorById)
                        .collect(Collectors.toList()))
                .prerequisites(List.of())
                .build();

        if (!finalTask && (!isExecutorsValid(task) || Objects.isNull(task.getProject())
                || !task.getProject().getExecutors().contains((Executor) currentUser))) {
            throw new RuntimeException("Executor is not in project.");
        }

        return taskRepository.save(task);
    }

    public Task update(TaskDto dto) {
        var currentUser = userService.getCurrentUser();
        var taskDB = taskRepository.getReferenceById(dto.getId());

        if (Objects.nonNull(dto.getDeadline())) taskDB.setDeadline(dto.getDeadline());
        if (Objects.nonNull(dto.getDescription())) taskDB.setDescription(dto.getDescription());
        if (Objects.nonNull(dto.getExecutorIds())) {
            taskDB.setExecutors(dto.getExecutorIds().stream()
                    .map(userService::getExecutorById)
                    .collect(Collectors.toList()));
        }

        if (!isExecutorsValid(taskDB) || Objects.isNull(taskDB.getProject())
                || !taskDB.getProject().getExecutors().contains((Executor) currentUser)) {
            throw new RuntimeException("Executor is not in project.");
        }

        return taskRepository.save(taskDB);
    }

    public  Task create(CreateTaskDto dto, Project project) {
        return create(dto, project,true);
    }

    public List<Task> getAllOfProject(Project project) {
        var currentUser = userService.getCurrentUser();

        var isUserInProject = switch (currentUser.getRole()) {
            case EXECUTOR -> project.getExecutors().contains((Executor) currentUser);
            case CLIENT -> Objects.equals(project.getClient(), (Client) currentUser);
        };

        if (!isUserInProject) {
            throw new RuntimeException("User is not in project.");
        }

        return taskRepository.findAll().stream()
                .filter(task -> Objects.equals(task.getProject().getId(), project.getId()))
                .collect(Collectors.toList());
    }

    private boolean isExecutorsValid(Task task) {
        return task.getProject().getExecutors().containsAll(task.getExecutors());
    }
}
