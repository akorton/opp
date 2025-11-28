package ru.tgu.opp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tgu.opp.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private Integer id;
    private String description;
    private LocalDateTime deadline;
    private List<Integer> ExecutorIds;
    private List<Integer> prerequisiteIds;
    private TaskStatus taskStatus;
    private Integer projectId;
}
