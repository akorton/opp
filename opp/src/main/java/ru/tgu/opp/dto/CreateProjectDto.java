package ru.tgu.opp.dto;

import lombok.Data;

@Data
public class CreateProjectDto {
    private final String title;
    private final String subject;
    private final String description;
}
