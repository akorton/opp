package ru.tgu.opp.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tgu.opp.dto.CreateProjectDto;
import ru.tgu.opp.dto.ProjectDto;
import ru.tgu.opp.model.Executor;
import ru.tgu.opp.model.Project;
import ru.tgu.opp.service.ProjectService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {
    private final ProjectService projectService;
    private final ModelMapper modelMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public ProjectDto create(@RequestBody ProjectDto dto) {
        var project = projectService.create(dto);
        return convertToDto(project);
    }

    @PostMapping
    @RequestMapping("/update")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ProjectDto update(@RequestBody ProjectDto dto) {
        var project = projectService.update(dto);
        return convertToDto(project);
    }

    @GetMapping
    public List<ProjectDto> getAll() {
        return projectService.getAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProjectDto convertToDto(Project project) {
        ProjectDto dto = modelMapper.map(project, ProjectDto.class);
        dto.setExecutorIds(project.getExecutors().stream()
                .map(Executor::getId)
                .collect(Collectors.toList()));
        return dto;
    }
}
