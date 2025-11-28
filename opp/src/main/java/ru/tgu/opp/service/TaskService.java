package ru.tgu.opp.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.tgu.opp.dto.CreateTaskDto;
import ru.tgu.opp.dto.TaskColumnDto;
import ru.tgu.opp.dto.TaskDto;
import ru.tgu.opp.model.*;
import ru.tgu.opp.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ModelMapper mapper;

    public TaskDto create(CreateTaskDto dto, Project project) {
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

        if (!isExecutorsValid(task) || Objects.isNull(task.getProject())
                || !task.getProject().getExecutors().contains((Executor) currentUser)) {
            throw new RuntimeException("Executor is not in project.");
        }

        return convertToDto(taskRepository.save(task));
    }

    public TaskDto update(TaskDto dto) {
        var currentUser = userService.getCurrentUser();
        var taskDB = taskRepository.getReferenceById(dto.getId());

        if (Objects.nonNull(dto.getDeadline())) taskDB.setDeadline(dto.getDeadline());
        if (Objects.nonNull(dto.getDescription())) taskDB.setDescription(dto.getDescription());
        if (Objects.nonNull(dto.getExecutorIds())) {
            taskDB.setExecutors(dto.getExecutorIds().stream()
                    .map(userService::getExecutorById)
                    .collect(Collectors.toList()));
        }

        taskDB.setStatus(getCorrectStatus(taskDB));
        if (Objects.nonNull(dto.getTaskStatus())) {
            if (!isValidStatus(taskDB, dto.getTaskStatus())) {
                throw new RuntimeException("Invalid task status.");
            }

            taskDB.setStatus(dto.getTaskStatus());
        }

        if (Objects.nonNull(dto.getPrerequisiteIds())) {
            taskDB.setPrerequisites(dto.getPrerequisiteIds().stream()
                    .map(this::getById)
                    .collect(Collectors.toList()));
        }

        if (!isExecutorsValid(taskDB) || Objects.isNull(taskDB.getProject())
                || !taskDB.getProject().getExecutors().contains((Executor) currentUser)) {
            throw new RuntimeException("Executor is not in project.");
        }

        if (!isPrerequisitesValid(taskDB)) {
            throw new RuntimeException("Prerequisites are not valid (probably cyclic dependency).");
        }

        return convertToDto(taskRepository.save(taskDB));
    }

    public Task getById(Integer id) {
        return taskRepository.getReferenceById(id);
    }

    private List<Task> getAllOfProject(Project project) {
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

    private TaskStatus getCorrectStatus(Task task) {
        if (task.getPrerequisites().stream().anyMatch(p -> p.getStatus() != TaskStatus.FINISHED)) {
            return TaskStatus.PREREQUISITES_NOT_MET;
        }

        if (task.getPrerequisitesOf().stream()
                .anyMatch(p -> p.getStatus() == TaskStatus.IN_PROGRESS || p.getStatus() == TaskStatus.FINISHED)) {
            return TaskStatus.FINISHED;
        }

        return task.getStatus();
    }

    private boolean isValidStatus(Task task, TaskStatus newStatus) {
        // these statuses are assigned automatically
        if (newStatus == TaskStatus.PREREQUISITES_NOT_MET || newStatus == TaskStatus.NOT_FINISHED) {
            return false;
        }

        // some prerequisites are not met
        if (task.getPrerequisites().stream().anyMatch(p -> p.getStatus() != TaskStatus.FINISHED)) {
            return false;
        }

        if (newStatus == TaskStatus.IN_PROGRESS && task.getPrerequisitesOf().stream()
                .anyMatch(p -> p.getStatus() == TaskStatus.IN_PROGRESS || p.getStatus() == TaskStatus.FINISHED)) {
            return false;
        }

        return true;
    }

    private boolean isExecutorsValid(Task task) {
        return task.getProject().getExecutors().containsAll(task.getExecutors());
    }

    public List<TaskColumnDto> getAllOfProjectWithColumn(Project project) {
        var tasks = getAllOfProject(project);
        var used = new HashMap<Integer, Integer>();
        var sortedTasks = new ArrayList<Task>();

        for (var i = 0; i < tasks.size(); ++i) {
            if (!dfs(tasks.get(i), used, sortedTasks)) {
                throw new RuntimeException("Cyclic dependency in prerequisites.");
            }
        }

        Collections.reverse(sortedTasks);

        var taskIdToCol = new HashMap<Integer, Integer>();
        var result = new ArrayList<TaskColumnDto>();

        for (var task: sortedTasks) {
            int maxCol = 0;
            for (var prereqOf: task.getPrerequisitesOf()) {
                maxCol = Math.max(maxCol, taskIdToCol.getOrDefault(prereqOf.getId(), 0));
            }

            taskIdToCol.put(task.getId(), maxCol + 1);
            result.add(TaskColumnDto.builder()
                    .taskDto(convertToDto(task))
                    .column(maxCol + 1)
                    .build());
        }

        return result;
    }

    private boolean dfs(Task task, HashMap<Integer, Integer> used, ArrayList<Task> sortedTasks) {
        var idx = task.getId();

        if (used.getOrDefault(idx, 0) == 1) {
            return false;
        }

        if (used.getOrDefault(idx, 0) == 2) {
            return true;
        }

        used.put(idx, 1);
        boolean result = true;

        for (var p: task.getPrerequisites()) {
            result = result && dfs(p, used, sortedTasks);
        }

        sortedTasks.add(task);
        used.put(idx, 2);
        return result;
    }

    private boolean isPrerequisitesValid(Task task) {
        if (Objects.isNull(task.getProject())) {
            return false;
        }

        var project = task.getProject();

        try {
            var tasks = getAllOfProjectWithColumn(project);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private TaskDto convertToDto(Task task) {
        TaskDto dto = mapper.map(task, TaskDto.class);
        dto.setTaskStatus(task.getStatus());
        dto.setExecutorIds(task.getExecutors().stream().map(Executor::getId).collect(Collectors.toList()));
        dto.setPrerequisiteIds(task.getPrerequisites().stream().map(Task::getId).collect(Collectors.toList()));
        return dto;
    }
}
