package ru.tgu.opp.dto;

import lombok.Builder;
import lombok.Data;
import ru.tgu.opp.model.Task;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDto {
    private Integer id;
    private String title;
    private String subject;
    private String description;
    private LocalDateTime deadline;
    private List<Integer> executorIds;
}
