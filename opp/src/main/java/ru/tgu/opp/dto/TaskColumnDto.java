package ru.tgu.opp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskColumnDto {
    private Integer column;
    private TaskDto taskDto;
}
