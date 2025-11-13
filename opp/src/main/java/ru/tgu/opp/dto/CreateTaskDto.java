package ru.tgu.opp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateTaskDto {
    private String description;
    private LocalDateTime deadline;
}
