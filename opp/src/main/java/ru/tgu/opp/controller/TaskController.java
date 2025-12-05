package ru.tgu.opp.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tgu.opp.dto.CreateTaskDto;
import ru.tgu.opp.dto.TaskColumnDto;
import ru.tgu.opp.dto.TaskDto;
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

    @PostMapping
    @PreAuthorize("hasAuthority('EXECUTOR')")
    public TaskDto create(@RequestBody CreateTaskDto dto) {
        return taskService.create(dto, projectService.getById(dto.getProjectId()));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('EXECUTOR')")
    public TaskDto update(@RequestBody TaskDto dto) {
        return taskService.update(dto);
    }

    @GetMapping
    public List<TaskColumnDto> getAllTasksOfProjectWithColumn(@RequestParam("project_id") Integer projectId) {
        return taskService.getAllOfProjectWithColumn(projectService.getById(projectId));
    }
}
