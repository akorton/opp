package ru.tgu.opp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tgu.opp.dto.CreateProjectDto;
import ru.tgu.opp.dto.ProjectDto;
import ru.tgu.opp.model.Project;
import ru.tgu.opp.repository.ProjectRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;

    public Project getById(Integer id) {
        return projectRepository.getReferenceById(id);
    }
    public Project create(ProjectDto dto) {
        var project = Project.builder()
                .title(dto.getTitle())
                .subject(dto.getSubject())
                .client(userService.getClientById(userService.getCurrentUser().getId()))
                .deadline(dto.getDeadline())
                .description(dto.getDescription())
                .executors(Objects.isNull(dto.getExecutorIds()) ? List.of() :
                        dto.getExecutorIds().stream()
                        .map(userService::getExecutorById)
                        .collect(Collectors.toList()))
                .build();

        return projectRepository.save(project);
    }

    public Project update(ProjectDto dto) {
        var projectDB = projectRepository.getReferenceById(dto.getId());

        if (Objects.nonNull(dto.getTitle())) projectDB.setTitle(dto.getTitle());
        if (Objects.nonNull(dto.getSubject())) projectDB.setSubject(dto.getSubject());
        if (Objects.nonNull(dto.getDeadline())) projectDB.setDeadline(dto.getDeadline());
        if (Objects.nonNull(dto.getDescription())) projectDB.setDescription(dto.getDescription());
        if (Objects.nonNull(dto.getExecutorIds())) {
            projectDB.setExecutors(dto.getExecutorIds().stream()
                    .map(userService::getExecutorById)
                    .collect(Collectors.toList()));
        }

        return projectRepository.save(projectDB);
    }

    public List<Project> getAll() {
        var projects = switch (userService.getCurrentUser().getRole()) {
            case EXECUTOR -> getAllExecutor();
            case CLIENT -> getAllClient();
        };

        projects.sort(Comparator.comparingInt(Project::getId));
        return projects;
    }

    private List<Project> getAllClient() {
        var clientId = userService.getCurrentUser().getId();
        return projectRepository.findAll().stream()
                .filter(project -> Objects.nonNull(project.getClient()) && Objects.equals(project.getClient().getId(), clientId))
                .collect(Collectors.toList());
    }

    private List<Project> getAllExecutor() {
        var executorId = userService.getCurrentUser().getId();

        return projectRepository.findAll().stream()
                .filter(project -> project.getExecutors().stream()
                        .anyMatch((executor -> Objects.equals(executor.getId(), executorId))))
                .collect(Collectors.toList());
    }
}
