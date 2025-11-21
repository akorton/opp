package ru.tgu.opp.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import ru.tgu.opp.dto.CreateTaskDto;
import ru.tgu.opp.dto.TaskDto;
import ru.tgu.opp.dto.TaskStatusDto;
import ru.tgu.opp.model.Executor;
import ru.tgu.opp.model.Task;
import ru.tgu.opp.service.ProjectService;
import ru.tgu.opp.service.TaskService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final ProjectService projectService;
    private final ModelMapper mapper;

    @PostMapping
    @PreAuthorize("hasAuthority('EXECUTOR')")
    public TaskDto create(@RequestBody CreateTaskDto dto) {
        var task = taskService.create(dto, projectService.getById(dto.getProjectId()), false);
        return convertToDto(task);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('EXECUTOR')")
    public TaskDto update(@RequestBody TaskDto dto) {
        var task = taskService.update(dto);
        return convertToDto(task);
    }

    @GetMapping("/project")
    public List<TaskDto> getAllTasksOfProject(@RequestParam("project_id") Integer projectId) {
        var tasks = taskService.getAllOfProject(projectService.getById(projectId));
        return tasks.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    private TaskDto convertToDto(Task task) {
        TaskDto dto = mapper.map(task, TaskDto.class);
        dto.setTaskStatus(task.getStatus().name());
        dto.setExecutorIds(task.getExecutors().stream().map(Executor::getId).collect(Collectors.toList()));
        dto.setPrerequisiteIds(task.getPrerequisites().stream().map(Task::getId).collect(Collectors.toList()));
        return dto;
    }
}
