package ru.tgu.opp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tgu.opp.dto.CreateProjectDto;
import ru.tgu.opp.dto.CreateTaskDto;
import ru.tgu.opp.dto.ProjectDto;
import ru.tgu.opp.model.Project;
import ru.tgu.opp.model.Task;
import ru.tgu.opp.model.TaskStatus;
import ru.tgu.opp.repository.ProjectRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final TaskService taskService;
    private final ProjectRepository projectRepository;
    private final UserService userService;

    public Project create(CreateProjectDto dto) {
        var finalTaskDto = CreateTaskDto.builder()
                .description("")
                .deadline(LocalDateTime.now())
                .build();

        var project = Project.builder()
                .title(dto.getTitle())
                .subject(dto.getSubject())
                .client(userService.getClientById(userService.getCurrentUser().getId()))
                .finalTask(taskService.create(finalTaskDto))
                .build();

        return projectRepository.save(project);
    }

    public Project update(ProjectDto dto) {
        var projectDB = projectRepository.getReferenceById(dto.getId());
        var finalTaskDB = projectDB.getFinalTask();

        if (Objects.nonNull(dto.getTitle())) projectDB.setTitle(dto.getTitle());
        if (Objects.nonNull(dto.getSubject())) projectDB.setSubject(dto.getSubject());
        if (Objects.nonNull(dto.getDeadline())) finalTaskDB.setDeadline(dto.getDeadline());
        if (Objects.nonNull(dto.getDescription())) finalTaskDB.setDescription(dto.getDescription());
        if (Objects.nonNull(dto.getExecutorIds())) {
            finalTaskDB.setExecutors(dto.getExecutorIds().stream()
                    .map(userService::getExecutorById)
                    .collect(Collectors.toList()));
        }

        projectDB.setFinalTask(finalTaskDB);
        return projectRepository.save(projectDB);
    }

    public List<Project> getAll() {
        return switch (userService.getCurrentUser().getRole()) {
            case EXECUTOR -> getAllExecutor();
            case CLIENT -> getAllClient();
        };
    }

    private List<Project> getAllClient() {
        var clientId = userService.getCurrentUser().getId();
        return projectRepository.findAll().stream()
                .filter(project -> Objects.equals(project.getClient().getId(), clientId))
                .collect(Collectors.toList());
    }

    private List<Project> getAllExecutor() {
        var executorId = userService.getCurrentUser().getId();

        return projectRepository.findAll().stream()
                .filter(project -> project.getFinalTask().getExecutors().stream()
                        .anyMatch((executor -> Objects.equals(executor.getId(), executorId))))
                .collect(Collectors.toList());
    }
}
